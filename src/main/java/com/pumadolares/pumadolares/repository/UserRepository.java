package com.pumadolares.pumadolares.repository;

import org.springframework.data.repository.CrudRepository;

import com.pumadolares.pumadolares.model.UserModel;

public interface UserRepository extends CrudRepository<UserModel, Integer> {

}
