package com.example.data_search.repository;

import com.example.data_search.entity.SearchQueryCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchQueryCacheRepository extends JpaRepository<SearchQueryCache, Long> {
    Optional<SearchQueryCache> findByNormalizedQuery(String normalizedQuery);
}