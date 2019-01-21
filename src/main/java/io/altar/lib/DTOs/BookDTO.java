package io.altar.lib.DTOs;

import java.util.ArrayList;

import io.altar.lib.model.Book;



public class BookDTO {
	private static final long serialVersionUID = 1L;

	public static final String GET_ALL_BOOKS_QUERY_NAME = "getAllBooks";
//	public static final String GET_ALL_DIFFERENT_ISBNS = "getAllDifferentIsbns";
	private String isbn;
	private ArrayList<Book> availableBooksWithThisIsbn;
	private ArrayList<Book> unavailableBooksWithThisIsbn;
	private int entriesNumberInHistoryRepository;
	public BookDTO(String isbn, ArrayList<Book> availableBooksWithThisIsbn,
			ArrayList<Book> unavailableBooksWithThisIsbn) {
		super();
		this.isbn = isbn;
		this.availableBooksWithThisIsbn = availableBooksWithThisIsbn;
		this.unavailableBooksWithThisIsbn = unavailableBooksWithThisIsbn;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public ArrayList<Book> getAvailableBooksWithThisIsbn() {
		return availableBooksWithThisIsbn;
	}
	public void setAvailableBooksWithThisIsbn(ArrayList<Book> availableBooksWithThisIsbn) {
		this.availableBooksWithThisIsbn = availableBooksWithThisIsbn;
	}
	public ArrayList<Book> getUnavailableBooksWithThisIsbn() {
		return unavailableBooksWithThisIsbn;
	}
	public void setUnavailableBooksWithThisIsbn(ArrayList<Book> unavailableBooksWithThisIsbn) {
		this.unavailableBooksWithThisIsbn = unavailableBooksWithThisIsbn;
	}
	public int getEntriesNumberInHistoryRepository() {
		return entriesNumberInHistoryRepository;
	}
	public void setEntriesNumberInHistoryRepository(int entriesNumberInHistoryRepository) {
		this.entriesNumberInHistoryRepository = entriesNumberInHistoryRepository;
	}
	
	
}
