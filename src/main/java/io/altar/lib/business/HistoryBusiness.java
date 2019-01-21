package io.altar.lib.business;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.altar.lib.DTOs.BookDTO;
import io.altar.lib.model.Book;
import io.altar.lib.model.History;
import io.altar.lib.model.User;
import io.altar.lib.repositories.BookRepository;
import io.altar.lib.repositories.HistoryRepository;
import io.altar.lib.repositories.UserRepository;



@Component
public class HistoryBusiness {
	@Inject
	BookRepository bookRepository;
	@Inject
	UserRepository userRepository;
	@Inject
	HistoryRepository historyRepository;
	@Inject
	UserBusiness userBusiness;

	@Transactional
	public History reserveBookHistory(History newHistory) {
		Book formerBook = bookRepository.findById(newHistory.getHistoryBook().getId());
		if(!userRepository.findById(newHistory.getHistoryUser().getId()).isActive()) {
			return null;
		}
		if (formerBook.getState().equals("available") && formerBook.isInLibraryPosession()) {
			formerBook.setState("reserved");
			bookRepository.update(formerBook);
			User formerUser = userRepository.findById(newHistory.getHistoryUser().getId());
			newHistory.setHistoryBook(formerBook);
			newHistory.setHistoryUser(formerUser);
			newHistory.setReservationDate(new Date());
			newHistory.setDeliveryDate(null);
			newHistory.setPickupDate(null);
//			String subject = "Reserva";
//			String content = "Exmo(a) " + formerUser.getName() + ", \n Confirmamos a reserva com sucesso do livro " + formerBook.getTitle() + " de " + formerBook.getAuthor() + ". \n Relembramos que terá 3 dias uteis para levantar o livro. \n \n Boas leituras ! \n \n com os melores cumprimentos, \n A equipa da Biblioteca 4.0";
//			userBusiness.sendEmail(formerUser.getEmail(), subject, content);
			return historyRepository.save(newHistory);
		} else if (!formerBook.getState().equals("available") && formerBook.isInLibraryPosession()) {
			if (historyRepository.getAllHistoryByBookIdQueryName(formerBook.getId()).size() < 2) {
				newHistory.setPreReservationDate(new Date());
				newHistory.setHistoryBook(formerBook);
				User formerUser = userRepository.findById(newHistory.getHistoryUser().getId());
				newHistory.setHistoryUser(formerUser);
//				String subject = "Pré-reserva";
//				String content = "Exmo(a) " + formerUser.getName() + ", \n Informamos que neste momento nao dispomos de nenhum exemplar disponível do título " + formerBook.getTitle() + " de " + formerBook.getAuthor() + ". \n Assim que um dos exemplares esteja disponível ficará reservado para si. \n Receberá um e-mail a informar quando o título estiver disponível para que o possa levantar. \n \n com os melores cumprimentos, \n A equipa da Biblioteca 4.0";
//				userBusiness.sendEmail(formerUser.getEmail(), subject, content);
				return historyRepository.save(newHistory);
			}
			return null;
		}
		return null;
	}

	@Transactional
	public History pickUpBook(Book bookToPickUp) {
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryBook().getId() == bookToPickUp.getId() && item.getPickupDate() == null) {
				if(!userRepository.findById(item.getHistoryUser().getId()).isActive()) {
					return null;
				}
				Book bookInUse = bookRepository.findById(bookToPickUp.getId());
				bookInUse.setState("inUse");
				bookRepository.update(bookInUse);
				item.setHistoryBook(bookInUse);
				item.setPickupDate(new Date());
				historyRepository.update(item);
				return item;
			}

		}
		return null;
	}

	@Transactional
	public History deliverBook(Book bookToDeliver) {
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryBook().getId() == bookToDeliver.getId() && item.getDeliveryDate() == null) {
				item.setDeliveryDate(new Date());
				Book bookAvailableAgain = bookRepository.findById(bookToDeliver.getId());
				bookAvailableAgain.setState("available");
				bookRepository.update(bookAvailableAgain);
				item.setHistoryBook(bookAvailableAgain);
				historyRepository.update(item);
				turnPrereservationIntoReservation(item.getHistoryBook().getId());
				return item;
			}
		}
		return null;
	}

	@Transactional
	public void turnPrereservationIntoReservation(long bookId) {
		Date newDate=new Date();
		for(History item:historyRepository.getAll()) {
			if(item.getHistoryBook().getId()==bookId && item.getReservationDate()==null) {
				item.setReservationDate(newDate);
			}
		}
	}
	
	public User getUserWithBook(long idBook) {
		for (History item : historyRepository.getAll()) {
			if (item.getDeliveryDate() == null && item.getReservationDate() != null
					&& item.getHistoryBook().getId() == idBook) {
				return item.getHistoryUser();
			}
		}
		return null;
	}

	public ArrayList<History> getBooksWithUser(long idUser) {
		ArrayList<History> resultToBooksWithUser = new ArrayList<History>();
		for (History item : historyRepository.getAll()) {
			if (item.getDeliveryDate() == null && item.getPickupDate() != null && item.getHistoryUser().getId() == idUser) {
				resultToBooksWithUser.add(item);
			}
		}
		return resultToBooksWithUser;
	}

	public ArrayList<History> getAllHstoryOfUser(long idUser) {
		ArrayList<History> resultAllHistory = new ArrayList<History>();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryUser().getId() == idUser) {
				resultAllHistory.add(item);
			}
		}
		return resultAllHistory;
	}

	@Transactional
	public History cancelReservation(long userId, long bookId) {
		History reservationToCancel = null;
		for (History item : historyRepository.getAll()) {
			if (item.getPickupDate() == null && item.getHistoryBook().getId() == bookId
					&& item.getHistoryUser().getId() == userId) {
				reservationToCancel=item;
			}
		}
		if (reservationToCancel == null) {
			return null;
		}
		Book formerBook = reservationToCancel.getHistoryBook();
		Date whenReservationIsCancelled = new Date();
		boolean isThisBookPrereserved = false;
		if (reservationToCancel.getReservationDate() != null) {
			for (History item : historyRepository.getAll()) {
				if (item.getHistoryBook().getId() == formerBook.getId() && item.getPickupDate() == null
						&& item.getReservationDate()==null) {
					isThisBookPrereserved = true;
					item.setReservationDate(whenReservationIsCancelled);
					System.out.println("ADEUUUUUUUUUUUUUUUUUUUUUUUUUS!");
				}
			}
			if (!isThisBookPrereserved) {
				System.out.println("OLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
				formerBook.setState("available");
				reservationToCancel.setHistoryBook(formerBook);
				bookRepository.update(formerBook);
			}
			reservationToCancel.setPickupDate(whenReservationIsCancelled);
			reservationToCancel.setDeliveryDate(whenReservationIsCancelled);
			historyRepository.update(reservationToCancel);
			return historyRepository.findById(reservationToCancel.getId());
		} else if (reservationToCancel.getReservationDate() == null) {
			reservationToCancel.setReservationDate(whenReservationIsCancelled);
			reservationToCancel.setPickupDate(whenReservationIsCancelled);
			reservationToCancel.setDeliveryDate(whenReservationIsCancelled);
			historyRepository.update(reservationToCancel);
			return historyRepository.findById(reservationToCancel.getId());
		}
		return null;
	}

	public ArrayList<History> getAllHistorys() {
		ArrayList<History> resultAllHistorys = new ArrayList<History>();
		if (historyRepository.getAll() != null) {
			for (History item : historyRepository.getAll()) {
				resultAllHistorys.add(item);
			}

		}
		return resultAllHistorys;

	}

	public ArrayList<BookDTO> getFiveMostReadIsbns() {
		ArrayList<BookDTO> bookDTOList=new ArrayList<BookDTO>();
		ArrayList<String >isbnList=new ArrayList<String>();
		isbnList=getIsbnList();
		for(String item:isbnList) {
			ArrayList<Book> availableBooks=new ArrayList<Book>();
			ArrayList<Book> unavailableBooks=new ArrayList<Book>();
			availableBooks=getAllAvailableBooks(item);
			unavailableBooks=getAllUnavailableBooks(item);
			BookDTO newBookDTO=new BookDTO(item,availableBooks,unavailableBooks);
			bookDTOList.add(newBookDTO);
		}
		for(BookDTO item:bookDTOList) {
			for(History item2:historyRepository.getAll()) {
				if(item2.getHistoryBook().getIsbn().equals(item.getIsbn())) {
					int numberOfFavorites=item.getEntriesNumberInHistoryRepository();
					numberOfFavorites++;
					item.setEntriesNumberInHistoryRepository(numberOfFavorites);
				}
			}
		}
		while(!arrayIsInDescendingOrder(bookDTOList)) {
			bookDTOList=rearranjeArrayList(bookDTOList);
		}
		while(bookDTOList.size()>5) {
			bookDTOList.remove(bookDTOList.size()-1);
		}
		return bookDTOList;
		
	}

	

	private ArrayList<BookDTO> rearranjeArrayList(ArrayList<BookDTO> bookDTOList) {
		for(int i=1;i<bookDTOList.size();i++) {
			if(bookDTOList.get(i).getEntriesNumberInHistoryRepository()>bookDTOList.get(i-1).getEntriesNumberInHistoryRepository()) {
				BookDTO savedBookDTO=bookDTOList.get(i);
				bookDTOList.set(i, bookDTOList.get(i-1));
				bookDTOList.set(i-1, savedBookDTO);
			}
		}
		return bookDTOList;
	}

	private boolean arrayIsInDescendingOrder(ArrayList<BookDTO> bookDTOList) {
		for(int i=1;i<bookDTOList.size();i++) {
			if(bookDTOList.get(i).getEntriesNumberInHistoryRepository()>bookDTOList.get(i-1).getEntriesNumberInHistoryRepository()) {
				return false;
			}
		}
		return true;
	}

	private ArrayList<Book> getAllUnavailableBooks(String isbn) {
		ArrayList<Book> unavailableBooks=new ArrayList<Book>();
		for(Book item:bookRepository.getAll()) {
			if(item.getIsbn().equals(isbn) && !item.getState().equals("available")) {
				unavailableBooks.add(item);
			}
		}
		return unavailableBooks;
	}

	private ArrayList<Book> getAllAvailableBooks(String isbn) {
		ArrayList<Book> allAvailableBooks=new ArrayList<Book>();
		for(Book item:bookRepository.getAll()) {
			if(item.getIsbn().equals(isbn) && item.getState().equals("available")) {
				allAvailableBooks.add(item);
			}
		}
		return allAvailableBooks;
	}

	private ArrayList<String> getIsbnList() {
		ArrayList<String> isbnList=new ArrayList<String>();
		for(Book item:bookRepository.getAll()) {
			if(thereIsNoSuchIsbn(item.getIsbn(),isbnList)) {
				isbnList.add(item.getIsbn());
			}
		}
		return isbnList;
	}

	private boolean thereIsNoSuchIsbn(String isbn, ArrayList<String> isbnList) {
		if(!isbnList.isEmpty()) {
			for(String item:isbnList) {
				if(item.equals(isbn)) {
					return false;
				}
			}
		}
		return true;
	}

	public ArrayList<History> getACertainAmountOfHistorysFromACertainIndex(int indexToStart, int amount) {
		ArrayList<History> allHistorys=new ArrayList<History>();
		ArrayList<History> smallArrayListHistorys=new ArrayList<History>();
		allHistorys=(ArrayList<History>) historyRepository.getAll();
		if(allHistorys.isEmpty()) {
			return null;
		}
		if(allHistorys.size()>indexToStart+amount-1) {
			for(int i=indexToStart;i<indexToStart+amount;i++) {
				smallArrayListHistorys.add(allHistorys.get(i));
			}
		} else if(allHistorys.size()<=indexToStart+amount-1 && allHistorys.size()>indexToStart) {
			for(int i=indexToStart;i<allHistorys.size();i++) {
				smallArrayListHistorys.add(allHistorys.get(i));
			}
		}
		return smallArrayListHistorys;
	}

}
