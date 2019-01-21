package io.altar.lib.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.altar.lib.DTOs.BookDTO;
import io.altar.lib.model.Book;
import io.altar.lib.repositories.BookRepository;
import io.altar.lib.repositories.HistoryRepository;
import io.altar.lib.repositories.UserRepository;



@Component
public class BookBusiness {
	@Inject
	BookRepository bookRepository;
	@Inject
	UserRepository userRepository;
	@Inject
	HistoryRepository historyRepository;
	@Inject
	HistoryBusiness historyBusiness;

	@Transactional
	public Book createBook(Book bookToCreate) {
		bookToCreate.setState("available");
		bookToCreate.setInLibraryPosession(true);
		return bookRepository.save(bookToCreate);
	}

	public Book getABook(long bookIdToGet) {
		if (bookRepository.findById(bookIdToGet) != null) {
			if (!bookRepository.findById(bookIdToGet).isInLibraryPosession()) {
				return null;
			}
			return bookRepository.findById(bookIdToGet);
		} else {
			return null;
		}
	}

	public ArrayList<Book> seeAllBooks() {
		ArrayList<Book> bookRepository1 = new ArrayList<Book>();
		for (Book item : bookRepository.getAll()) {
			if (item.isInLibraryPosession()) {
				bookRepository1.add(item);
			}
		}
		return bookRepository1;
	}

	@Transactional
	public Book removeBook(long bookIdToRemove) {
		if (bookRepository.findById(bookIdToRemove) != null) {
			Book bookToRemove = bookRepository.findById(bookIdToRemove);
			bookToRemove.setInLibraryPosession(false);
			bookRepository.update(bookToRemove);
			return bookRepository.findById(bookIdToRemove);
		}
		return null;
	}

	public ArrayList<BookDTO> generalResearchForBook(String keyExpression) {
		ArrayList<BookDTO> allBookDTOs = new ArrayList<BookDTO>();
		ArrayList<BookDTO> resultToKeyExpression = new ArrayList<BookDTO>();
		allBookDTOs = getAllBooksByISbnSpecial();
		if (!allBookDTOs.isEmpty()) {
			for (BookDTO item : allBookDTOs) {
				if (!item.getAvailableBooksWithThisIsbn().isEmpty()) {
					Book bookToSearch = item.getAvailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(bookToSearch.getTitle(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getDescription(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getAuthor(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getTopic(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getIsbn(), keyExpression)) {
						resultToKeyExpression.add(item);
					}
				} else {
					Book bookToSearch = item.getUnavailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(bookToSearch.getTitle(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getDescription(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getAuthor(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getTopic(), keyExpression)
							|| matchStringToSubstring(bookToSearch.getIsbn(), keyExpression)) {
						resultToKeyExpression.add(item);
					}
				}
			}
			return resultToKeyExpression;
		}
		return new ArrayList<BookDTO>();
	}

	/**
	 * função insensível a maiúculas e minúsculas que returna se uma superstring
	 * contem uma substring
	 * 
	 * @param Superstring - string que se quer verificar se contem a substring
	 * @param substring   - string que se quer verificar se está contida na
	 *                    superstring
	 * @return - true se superstring contiver substring e falso caso contrário
	 * 
	 */
	public boolean matchStringToSubstring(String Superstring, String substring) {
		if (Superstring != null && substring != null) {
			if (Superstring.toLowerCase().contains(substring.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public Book updateBook(Book updatedBook) {
		if (bookRepository.findById(updatedBook.getId()) != null) {
			updatedBook.setState(bookRepository.findById(updatedBook.getId()).getState());
			updatedBook.setInLibraryPosession(true);
			return bookRepository.update(updatedBook);
		} else {
			return null;
		}

	}

	public ArrayList<BookDTO> researchBookByTitle(String titleToTest) {
		ArrayList<BookDTO> resultToTitleToTest = new ArrayList<BookDTO>();
		ArrayList<BookDTO> allBookDTOs = getAllBooksByISbnSpecial();
		if (!allBookDTOs.isEmpty()) {
			for (BookDTO item : allBookDTOs) {
				if (!item.getAvailableBooksWithThisIsbn().isEmpty()) {
					Book bookToTest = item.getAvailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(bookToTest.getTitle(), titleToTest)) {
						resultToTitleToTest.add(item);
					}
				} else {
					Book bookToTest = item.getUnavailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(bookToTest.getTitle(), titleToTest)) {
						resultToTitleToTest.add(item);
					}
				}
			}
		}
		return resultToTitleToTest;
	}

	public ArrayList<BookDTO> researchBookByDescription(String bookDescriptionToTest) {
		ArrayList<BookDTO> allBookDTOs = getAllBooksByISbnSpecial();
		ArrayList<BookDTO> resultToDescriptionToTest = new ArrayList<BookDTO>();
		if (!allBookDTOs.isEmpty()) {
			for (BookDTO item : allBookDTOs) {
				if (!item.getAvailableBooksWithThisIsbn().isEmpty()) {
					Book searchedBook = item.getAvailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getDescription(), bookDescriptionToTest)) {
						resultToDescriptionToTest.add(item);
					}
				} else {
					Book searchedBook = item.getUnavailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getDescription(), bookDescriptionToTest)) {
						resultToDescriptionToTest.add(item);
					}
				}
			}
		}
		return resultToDescriptionToTest;
	}

	public ArrayList<BookDTO> researchBookByAuthor(String authorToTest) {
		ArrayList<BookDTO> allBookDtos = getAllBooksByISbnSpecial();
		ArrayList<BookDTO> resultToAuthorToTest = new ArrayList<BookDTO>();
		if (!allBookDtos.isEmpty()) {
			for (BookDTO item : allBookDtos) {
				if (!item.getAvailableBooksWithThisIsbn().isEmpty()) {
					Book searchedBook = item.getAvailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getAuthor(), authorToTest)) {
						resultToAuthorToTest.add(item);
					}
				} else {
					Book searchedBook = item.getUnavailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getAuthor(), authorToTest)) {
						resultToAuthorToTest.add(item);
					}
				}
			}
		}
		return resultToAuthorToTest;
	}

	public ArrayList<BookDTO> researchBookByTopic(String topicToTest) {
		ArrayList<BookDTO> allBookDtos = getAllBooksByISbnSpecial();
		ArrayList<BookDTO> resultToTopicToTest = new ArrayList<BookDTO>();
		if (!allBookDtos.isEmpty()) {
			for (BookDTO item : allBookDtos) {
				if (!item.getAvailableBooksWithThisIsbn().isEmpty()) {
					Book searchedBook = item.getAvailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getTopic(), topicToTest)) {
						resultToTopicToTest.add(item);
					}
				} else {
					Book searchedBook = item.getUnavailableBooksWithThisIsbn().get(0);
					if (matchStringToSubstring(searchedBook.getTopic(), topicToTest)) {
						resultToTopicToTest.add(item);
					}
				}
			}
		}
		return resultToTopicToTest;
	}

	public ArrayList<Book> getAllAvailableBooks() {
		ArrayList<Book> resultToAvailableBooks = new ArrayList<Book>();
		for (Book item : bookRepository.getAll()) {
			if (item.getState().equals("available") && item.isInLibraryPosession()) {
				resultToAvailableBooks.add(item);
			}
		}
		return resultToAvailableBooks;
	}

	public ArrayList<Book> researchBookByISBN(String ISBNToTest) {
		ArrayList<Book> resultISBNToTest = new ArrayList<Book>();
		for (Book item : bookRepository.getAll()) {
			if (matchStringToSubstring(item.getIsbn(), ISBNToTest) && item.isInLibraryPosession()) {
				resultISBNToTest.add(item);
			}
		}
		return resultISBNToTest;
	}

	public ArrayList<BookDTO> getAllBooksByISbnSpecial() {
		ArrayList<BookDTO> bookDTOList = new ArrayList<BookDTO>();
		ArrayList<String> isbnList = new ArrayList<String>();
		isbnList = obtainIsbnList();
		if (!isbnList.isEmpty()) {
			for (String item : isbnList) {
				ArrayList<Book> availableBooks = new ArrayList<Book>();
				ArrayList<Book> unavailableBooks = new ArrayList<Book>();
				availableBooks = getAllAvailableBooksWithThisIsbn(item);
				unavailableBooks = getAllUnavailableBooksWithThisIsbn(item);
				BookDTO newBookDTO = new BookDTO(item, availableBooks, unavailableBooks);
				bookDTOList.add(newBookDTO);
			}
		}
		return bookDTOList;
	}

	private ArrayList<Book> getAllUnavailableBooksWithThisIsbn(String isbn) {
		ArrayList<Book> booksWithThisIsbnAndUnavailable = new ArrayList<Book>();
		if (bookRepository.getAll() != null) {
			for (Book item : bookRepository.getAll()) {
				if (!item.getState().equals("available") && item.getIsbn().equals(isbn)
						&& item.isInLibraryPosession()) {
					booksWithThisIsbnAndUnavailable.add(item);
				}
			}
		}

		return booksWithThisIsbnAndUnavailable;
	}

	private ArrayList<Book> getAllAvailableBooksWithThisIsbn(String isbn) {
		ArrayList<Book> booksWithThisIsbnAndAvailable = new ArrayList<Book>();
		if (bookRepository.getAll() != null) {
			for (Book item : bookRepository.getAll()) {
				if (item.getState().contentEquals("available") && item.getIsbn().equals(isbn)
						&& item.isInLibraryPosession()) {
					booksWithThisIsbnAndAvailable.add(item);
				}
			}
		}
		return booksWithThisIsbnAndAvailable;
	}

	private ArrayList<String> obtainIsbnList() {
		ArrayList<String> isbnList = new ArrayList<String>();
		if (bookRepository.getAll() != null) {
			for (Book item : bookRepository.getAll()) {
				if (thereIsNoSuchIsbn(item.getIsbn(), isbnList) && item.isInLibraryPosession()) {
					isbnList.add(item.getIsbn());
				}
			}
		}
		return isbnList;
	}

	private boolean thereIsNoSuchIsbn(String isbn, ArrayList<String> isbnList) {
		if (!isbnList.isEmpty()) {
			for (String item : isbnList) {
				if (item.contentEquals(isbn)) {
					return false;
				}
			}
		}
		return true;
	}

	public ArrayList<BookDTO> getACertainAmountOfBookDTOFromACertainIndex(int indexToStart, int amount) {
		ArrayList<BookDTO> allBookDTOs=new ArrayList<BookDTO>();
		ArrayList<BookDTO> smallArrayListBookDTO=new ArrayList<BookDTO>();
		allBookDTOs=getAllBooksByISbnSpecial();
		if(allBookDTOs.isEmpty()) {
			return null;
		}
		if(allBookDTOs.size()>indexToStart+amount-1) {
			for(int i=indexToStart;i<indexToStart+amount;i++) {
				smallArrayListBookDTO.add(allBookDTOs.get(i));
			}
		} else if(allBookDTOs.size()<=indexToStart+amount-1 && allBookDTOs.size()>indexToStart) {
			for(int i=indexToStart;i<allBookDTOs.size();i++) {
				smallArrayListBookDTO.add(allBookDTOs.get(i));
			}
		}
		return smallArrayListBookDTO;
	}
}
