package com.pumadolares.pumadolares.repository;

import org.springframework.data.repository.CrudRepository;

import com.pumadolares.pumadolares.model.JobModel;

public interface JobRepository extends CrudRepository<JobModel, Integer> {
  boolean existsByName(String name);
}
