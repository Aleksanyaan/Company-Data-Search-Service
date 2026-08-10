package com.example.data_search.repository;

import com.example.data_search.entity.PersonWithSignificantControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonWithSignificantControlRepository extends JpaRepository<PersonWithSignificantControl, Long> {
}