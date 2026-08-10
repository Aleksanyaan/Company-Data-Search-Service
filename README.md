# Company Data Search Service

A Spring Boot service that searches UK Companies House for matching companies, scrapes their
overview, officers, and persons-with-significant-control data, caches results in PostgreSQL, and
returns everything as structured JSON.

## How to run it

### Prerequisites
- Java 17+
- Maven
- PostgreSQL 16 (installed locally, or run via Docker — see below)

### 1. Set up the database

Connect as the Postgres superuser:
```bash
psql -U postgres
```

Create the database and app user:
```sql
CREATE DATABASE companysearch;
CREATE USER companysearch WITH PASSWORD 'companysearch';
GRANT ALL PRIVILEGES ON DATABASE companysearch TO companysearch;
```

**Important — this next step is required, not optional.** On PostgreSQL 15+, granting
privileges on the *database* does not automatically grant privileges on the `public` *schema*
inside it. Without this, Spring Boot/Hibernate will fail on startup with
`permission denied for schema public`. Connect to the new database specifically, then grant
schema-level access:
```sql
\c companysearch
GRANT ALL ON SCHEMA public TO companysearch;
GRANT CREATE ON SCHEMA public TO companysearch;
```

You can confirm it worked with:
```sql
\dn+
```
You should see `companysearch=UC` listed under the `public` schema's access privileges. If it's
missing, table creation on app startup will fail.


### 2. Configure `src/main/resources/application.yaml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/companysearch
    driver-class-name: org.postgresql.Driver
    username: companysearch
    password: companysearch
  jpa:
    hibernate:
      ddl-auto: update

companies-house:
  base-url: https://find-and-update.company-information.service.gov.uk
  user-agent: "YourName-CompanySearchService/1.0 (contact: your-email@example.com)"
  request-delay-ms: 500
  max-companies-per-query: 100
  connect-timeout-ms: 10000
  search-cache-ttl-hours: 24
  company-cache-ttl-hours: 168
```
Tables are created automatically on first startup (`ddl-auto: update`) — no manual schema step needed.

### 3. Run it
```bash
mvn spring-boot:run
```
The service starts on `http://localhost:8080`.

## API

### `GET /api/search`

**Query parameters:**
| Param | Required | Description |
|---|---|---|
| `q` | yes | Search term. Repeatable — pass `q` multiple times to search several terms in one call, results are combined and deduplicated by company number. |
| `forceRefresh` | no (default `false`) | If `true`, bypasses both cache layers and re-scrapes everything fresh. |

**Example request:**
```bash
curl "http://localhost:8080/api/search?q=picsart"
```

**Example response:**
```json
{
  "queries": ["picsart"],
  "resultCount": 3,
  "forceRefresh": false,
  "companies": [
    {
      "companyNumber": "13741239",
      "companyName": "PICSART UK LIMITED",
      "status": "Active",
      "companyType": "Private limited Company",
      "incorporationDate": "2021-11-12",
      "dissolutionDate": null,
      "registeredOfficeAddress": "International House, 45-55 Commercial Street, London, United Kingdom, E1 6BD",
      "sicCodes": "62011 - Ready-made interactive leisure and entertainment software development",
      "lastScrapedAt": "2026-08-08T12:54:54.969981Z",
      "officers": [
        {
          "name": "DING, Wei",
          "role": "Director",
          "appointedOn": "2025-11-01",
          "resignedOn": null,
          "nationality": "American",
          "occupation": null
        }
      ],
      "personsWithSignificantControl": [
        {
          "name": "Mr Hovhannes Avoyan",
          "natureOfControl": "Ownership of shares – More than 25% but not more than 50%",
          "notifiedOn": "2021-11-12"
        }
      ]
    }
  ]
}
```

**Multiple queries in one call:**
```bash
curl "http://localhost:8080/api/search?q=picsart&q=miro"
```

**Force a fresh scrape, bypassing cache:**
```bash
curl "http://localhost:8080/api/search?q=picsart&forceRefresh=true"
```

## Caching & freshness strategy

Two independent cache layers, checked separately:

1. **Query-level cache** (`search_query_cache` table) — when a query is searched, the list of
   matched company numbers is stored with a timestamp. A repeat search for the same normalized
   query (trimmed, lowercased) within `search-cache-ttl-hours` (default 24h) reuses that list
   instead of re-scraping the Companies House search page.

2. **Company-level freshness** (`companies.last_scraped_at`) — even when the query cache hits,
   each individual company is checked against `company-cache-ttl-hours` (default 7 days). If a
   specific company's data is older than that, it gets re-scraped on its own — regardless of
   whether the search itself was cached.

This means a repeat search is fast (no network calls at all if everything is fresh), but a company
that changes status between searches won't silently stay wrong for weeks — only the query-level
"which companies matched" list is long-lived; the actual company data has its own, shorter
expiry.

`forceRefresh=true` bypasses both layers entirely for a given request.

A per-query lock (`ConcurrentHashMap` of query → lock object) prevents two concurrent requests for
the same uncached query from triggering duplicate scrapes simultaneously.

## Politeness

- Every HTTP request goes through a single `PoliteHttpClient`, which is the only class allowed to
  call Jsoup directly. It sets a descriptive `User-Agent` and enforces a minimum delay
  (`request-delay-ms`, default 500ms) between consecutive requests, synchronized globally.
- Search result pagination stops as soon as a page returns no results, or no *new* results (to
  avoid looping if Companies House returns repeated/stale pages past what actually exists).
- Scraping is capped at `max-companies-per-query` (default 100) company records per query.

## What I did not fully finish / would improve with more time

- **Tests.** I didn't write unit tests for the parsers or the dedup logic, even though the
  parsing methods are written as pure functions (HTML in, entity out) specifically so they'd be
  easy to test later against saved fixture HTML.
- **Docker.** I set up Postgres as a local install rather than Docker Compose. With more time I'd
  add a `docker-compose.yml` so the whole project — app and DB — could be started with one
  command, instead of requiring a manual local Postgres setup.
- **Selector fragility.** Several of my selectors were initially wrong — guessed from what looked
    like reasonable class/ID names rather than the real markup — which caused fields like company
    name, address, and officer role to silently come back as `null` or empty instead of erroring.
    I only caught this by comparing the JSON output against the real page and fixing selectors one
    field at a time. The current selectors are confirmed against the companies I actually tested
    with, but I haven't verified them against every company type (e.g. LLPs, overseas companies),
    so a similar silent-failure could still happen on a page structure I haven't seen.

## Hardest part

The hardest part for me was HTML parsing. I'd never used Jsoup before, and Companies House has no public API for this, so there was no schema to work from. I had to inspect the real pages by hand and guess selectors, and my first guesses were mostly wrong. The tricky part is that Jsoup doesn't error on a bad selector. It just returns an empty result, so my code ran fine but half the fields came back silently blank. I only caught it by comparing actual JSON output against the real page.

What fixed it was ditching guesswork and matching selectors directly against saved HTML source, things like #roa-address, #cessation-date, and the officer div[class^=appointment-] blocks. I also learned the hard way that a pagination loop without a proper stop condition can misbehave badly against a real site, mine fired thousands of requests before I added a check to stop once a page returned no new results.