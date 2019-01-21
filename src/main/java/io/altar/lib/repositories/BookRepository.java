package io.altar.lib.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.altar.lib.model.Book;



@Repository
public class BookRepository extends EntityRepository<Book> {

	@Override
	protected Class<Book> getEntityClass() {
		return Book.class;
	}

	@Override
	protected String getAllEntityQueryName() {
		return Book.GET_ALL_BOOKS_QUERY_NAME;
	}
	
	
	public List<Book> getAllDifferentIsbns() {
		return em.createNativeQuery("SELECT id, author, description, inLibraryPosession, isbn, location, photoLink, state, title, topic FROM (select * from librarymanagement.Book) A inner join (select min(id) as id_ from librarymanagement.Book group by isbn) B ON A.id = B.id_;", Book.class).getResultList();
//		return em.createNamedQuery(Book.GET_ALL_DIFFERENT_ISBNS,Book.class).getResultList();
	}

}
