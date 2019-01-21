package io.altar.lib.repositories;

import org.springframework.stereotype.Repository;

import io.altar.lib.model.User;


@Repository
public class UserRepository extends EntityRepository <User>{

	public UserRepository() {
	}
	
		
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}


	@Override
	protected String getAllEntityQueryName() {
		return "getAllUsers";
	}

	
}
