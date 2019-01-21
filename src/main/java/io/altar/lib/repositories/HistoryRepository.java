package io.altar.lib.repositories;


import java.util.List;

import org.springframework.stereotype.Repository;

import io.altar.lib.model.History;



@Repository
public class HistoryRepository extends EntityRepository<History>{
	
	public HistoryRepository() {
	}
	
	
	@Override
	protected Class<History> getEntityClass() {
		return History.class;
	}

	@Override
	protected String getAllEntityQueryName() {
		return History.GET_ALL_HISTORYS_QUERY_NAME;
	}
	

	public List<History> getAllHistoryByBookIdQueryName(long id) {
		return em.createNamedQuery(History.GET_ALL_HISTORYS_BY_BOOK_ID, History.class ).setParameter("id", id).getResultList();
	
	}

}
