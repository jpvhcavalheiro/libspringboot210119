package io.altar.lib.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sendgrid.*;

import io.altar.lib.model.Book;
import io.altar.lib.model.History;
import io.altar.lib.model.User;
import io.altar.lib.repositories.BookRepository;
import io.altar.lib.repositories.HistoryRepository;
import io.altar.lib.repositories.UserRepository;


@Component
public class UserBusiness {
	@Inject
	UserRepository userRepository;
	@Inject
	BookRepository bookRepository;
	@Inject
	HistoryRepository historyRepository;

	/**
	 * 
	 * @param input user que se pretende criar
	 * @return user criado a partir da base de dados
	 */
	@Transactional
	public User createUser(User input) {
		input.setFavouriteIsbns(new ArrayList<String>());
		input.setActive(true);
		if (userRepository.getAll() != null) {
			for (User item : userRepository.getAll()) {
				if (item.getEmail().equals(input.getEmail())) {
					return null;
				}
			}
		}
//		String subject = "Bem-vindo";
//		String content = "Bem-vindo " + input.getName()
//				+ ", \n Obrigado por se ter registado como utilizador na nossa biblioteca. \n Navegue até à nossa página e comece a escolher as suas próximas aventuras literárias. \n Boas leituras ! \n \n com os melores cumprimentos, \n A equipa da Biblioteca 4.0";
//		sendEmail(input.getEmail(), subject, content);
		return userRepository.save(input);
	}

	/**
	 * 
	 * @param userId id do utilizador do qual se pretende obter informação
	 * @return null se o não existir o utilizador e o user caso contrário
	 */
	public User getUser(long userId) {
		User temp = searchUserById(userId);
		if (temp != null) {
			return temp;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param userIdWhoseReservationsAreToBeCancelled id do utilizador cujas reservas
	 * se pretende cancelar
	 */
	@Transactional
	public void cancelReservations(long userIdWhoseReservationsAreToBeCancelled) {
		Date dateWhenReservationIsCancelled = new Date();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryUser().getId() == userIdWhoseReservationsAreToBeCancelled
					&& item.getReservationDate() != null && item.getPickupDate() == null) {
				item.setPickupDate(dateWhenReservationIsCancelled);
				item.setDeliveryDate(dateWhenReservationIsCancelled);
				historyRepository.update(item);
				if (isThereAPrereservationOfThisBook(item.getHistoryBook().getId())) {
					prereservationOfThisBookTurnReservationsOfThisBook(item.getHistoryBook().getId());
				} else {
					Book newBook = bookRepository.findById(item.getHistoryBook().getId());
					newBook.setState("available");
					bookRepository.update(newBook);
				}
			}
		}
	}

	/**
	 * 
	 * @param bookId id do livro do qual se pretende transformar a pré-reserva em
	 * reserva
	 */
	@Transactional
	private void prereservationOfThisBookTurnReservationsOfThisBook(long bookId) {
		Date newDate = new Date();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryBook().getId() == bookId && item.getReservationDate() == null) {
				item.setReservationDate(newDate);
				historyRepository.update(item);
				break;
			}
		}
	}

	/**
	 * 
	 * @param bookId id do livro do qual se pretende descobrir se existe uma pré-reserva
	 * associada
	 * @return true caso exista uma pré-reserva deste livro e false caso contrário
	 */
	@Transactional
	private boolean isThereAPrereservationOfThisBook(long bookId) {
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryBook().getId() == bookId && item.getReservationDate() == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param userId id do utilizador do qual se pretende cancelar a pré-reserva
	 */
	@Transactional
	public void cancelPrereservations(long userId) {
		Date newDate = new Date();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryUser().getId() == userId && item.getReservationDate() == null) {
				item.setReservationDate(newDate);
				item.setPickupDate(newDate);
				item.setDeliveryDate(newDate);
				historyRepository.update(item);
			}
		}
	}

	/**
	 * 
	 * @param userId id do utilizador para o qual se pretende mudar o estado (reactivar ou
	 * banir)
	 * @param newUserState boolean que assume o valor de true caso se queira reactivar o
	 * utilizador e false caso se queira banir
	 * @return o utlizador que foi reactivado ou banido
	 */
	@Transactional
	public User changeUserActiveState(long userId, boolean newUserState) {
		User user = userRepository.findById(userId);
		if(user==null) {
			return null;
		}
		user.setActive(newUserState);
		Date dateWhenUserIsBanished = new Date();
		if (!newUserState) {
			user.setAdmin(false);
			cancelReservations(userId);
			cancelPrereservations(userId);
			cancelBooksInUseButNotReturned(userId);
		}
		userRepository.update(user);
		return userRepository.findById(userId);
	}

	/**
	 * 
	 * @param userId id do utlizador para o qual se pretende obter e dar como perdidos os
	 * livros que foram levantados mas não devolvidos
	 */
	@Transactional
	private void cancelBooksInUseButNotReturned(long userId) {
		Date newDate = new Date();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryUser().getId() == userId && item.getPickupDate() != null
					&& item.getDeliveryDate() == null) {
				item.setDeliveryDate(newDate);
				cancelPrereservations(item.getHistoryBook().getId(), "book");
				Book newBook = bookRepository.findById(item.getHistoryBook().getId());
				newBook.setInLibraryPosession(false);
				bookRepository.update(newBook);
			}
		}
	}

	/**
	 * 
	 * @param bookId id do livro para o qual se pretende cancelar as pré-reservas
	 */
	private void cancelPrereservations(long bookId, String string) {
		Date newDate = new Date();
		for (History item : historyRepository.getAll()) {
			if (item.getHistoryBook().getId() == bookId && item.getReservationDate() == null) {
				item.setReservationDate(newDate);
				item.setPickupDate(newDate);
				item.setDeliveryDate(newDate);
				historyRepository.update(item);
				break;
			}
		}

	}

	/**
	 * Update de todos os parâmetros do utilizador
	 * 
	 * @param input
	 * @return
	 */

	@Transactional
	public User updateUser(User input, long userId) {
		if(userRepository.findById(userId)==null) {
			return null;
		}
		input.setFavouriteIsbns(userRepository.findById(userId).getFavouriteIsbns());
		if(input.getFavouriteIsbns()==null) {
			input.setFavouriteIsbns(new ArrayList<String>());
			input.setId(userId);
			input.setAdmin(userRepository.findById(userId).isAdmin());
			input.setActive(userRepository.findById(userId).isActive());
		}
		if(!thereIsAnotherEmailLikeThis(input.getEmail(),userId)) {
			
			System.out.println(thereIsAnotherEmailLikeThis(input.getEmail(),userId));
			return userRepository.update(input);
			
		}
		return null;
	}

	/**
	 * 
	 * @param email atributo do utilizador
	 * @param userId id do utilizador
	 * @return true casoexita algum outro utilizador com o mesmo email e false 
	 * caso contrário
	 */
	@Transactional
	private boolean thereIsAnotherEmailLikeThis(String email, long userId) {
		for(User item:userRepository.getAll()) {
			System.out.println("ID="+item.getId());
			if(item.getEmail().equals(email) && item.getId()!=userId) {
				System.out.println("ADEUUUUUUUUUUUS");
				System.out.println("ID="+item.getId());
				return true;
			}
		}
		System.out.println("OLAAAAAAAAAAAAAAAAAA");
		return false;
	}

	/**
	 * Pesquisa todos os utilizadores
	 * 
	 * @return uma lista com todos os utilizadores
	 */

	public List<User> getAll() {
		return userRepository.getAll();
	}

	/**
	 * Pesquisa utilizadoresporId
	 * 
	 * @param userId
	 * @return utilizador
	 */

	public User searchUserById(long userId) {
		User temp = userRepository.findById(userId);
		return temp;
	}

	/**
	 * Pesquisa de utilizadores por nome
	 * 
	 * @param name
	 * @return List de userDto com esse nome
	 */

	public List<User> searchUserByName(String name) {
		List<User> allUsers = userRepository.getAll();
		List<User> listToBeReturned = new ArrayList<User>();
		for (User userInit : allUsers) {
			if (userInit.getName().toLowerCase().contains(name.toLowerCase())) {
				listToBeReturned.add(userInit);
			}
		}
		return listToBeReturned;
	}

	/**
	 * Pesquisa de utilizadores por NIP
	 * 
	 * @param nip
	 * @return List de userDto que contenham nip
	 */
	public List<User> searchUserByNip(String nip) {
		List<User> allUsers = userRepository.getAll();
		List<User> listToBeReturned = new ArrayList<User>();
		for (User userInit : allUsers) {
			if (userInit.getNip().toLowerCase().contains(nip.toLowerCase())) {
				listToBeReturned.add(userInit);
			}
		}
		return listToBeReturned;
	}

	/**
	 * 
	 * @param email
	 * @return List de userDto que contenham email
	 */
	public List<User> searchUserByEmail(String email) {
		List<User> allUsers = userRepository.getAll();
		List<User> listToBeReturned = new ArrayList<User>();
		for (User userInit : allUsers) {
			if (userInit.getEmail().toLowerCase().contains(email.toLowerCase())) {
				listToBeReturned.add(userInit);
			}
		}
		return listToBeReturned;
	}

	/**
	 * Verifica se um utilizador foi banido
	 * @param userIdToTest id do utlizador
	 * @return true caso o utilizador esteja activo e falso caso tenha sido banido
	 */
	@Transactional
	public boolean isUserActive(long userIdToTest) {
		User userToTest = userRepository.findById(userIdToTest);
		if (userToTest.isActive()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Transforma utilizador em Administrador
	 * 
	 * @param id
	 * @return UserDTo
	 */

	@Transactional
	public User turnUserToAdmin(long id) {
		if(userRepository.findById(id)==null) {
			return null;
		}
		if (isUserActive(id)) {
			User temp = searchUserById(id);
			temp.setAdmin(true);
			userRepository.update(temp);
			return userRepository.findById(id);
		}
		return null;
	}

	/**
	 * 
	 * @param idUser id od utilizador
	 * @param isbn atributo do livro
	 * @return adiciona este isbn ä lista de favoritos associado ao idUser
	 */
	@Transactional
	public User addBookToFavourites(long idUser, String isbn) {
		if(userRepository.findById(idUser)==null) {
			return null;
		}
		User user = userRepository.findById(idUser);
		if(user.getFavouriteIsbns()==null) {
			user.setFavouriteIsbns(new ArrayList<String>());
		}
		if(!thereIsThisIsbn(isbn)) {
			return null;
		}
		
		if (!isfavourite(isbn, user.getFavouriteIsbns())) {
			user.getFavouriteIsbns().add(isbn);
			userRepository.update(user);
			return user;
		}
		return null;

	}

	/**
	 * 
	 * @param isbn numero de identificação do tipo de livro
	 * @return true caso exista algum livro com este isbn e false caso contrário
	 */
	private boolean thereIsThisIsbn(String isbn) {
		for(Book item:bookRepository.getAll()) {
			if(item.getIsbn().equals(isbn)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param isbn número de identificação do livro
	 * @param favouritesList lista de isbns associado a um determinado utlizador
	 * @return true caso este isbn exista na favouritesList e false caso contrário
	 */
	@Transactional
	public boolean isfavourite(String isbn, List<String> favouritesList) {
		if(favouritesList==null) {
			favouritesList=new ArrayList<String>();
		}
		if (!favouritesList.isEmpty()) {
			for (String item : favouritesList) {
				if (item.equals(isbn)) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * 
	 * @param idUser id do utlizador
	 * @return um exemplo de livro por cada isbn favorito do utilizador cujo id é idUser
	 */
	public List<Book> getAllFavourites(long idUser) {
		User user=userRepository.findById(idUser);
		if(user==null) {
			return null;
		}
		ArrayList<Book> allFavouriteBooks=new ArrayList<>();
		ArrayList<String> isbnFavouriteList= (ArrayList<String>) user.getFavouriteIsbns();
		if(!isbnFavouriteList.isEmpty()) {
			for(String item:isbnFavouriteList) {
				Book favouriteBook=getABook(item);
				if(favouriteBook!=null) {
					allFavouriteBooks.add(favouriteBook);
				}
			}
		}
		return allFavouriteBooks;
	}

	/**
	 * 
	 * @param isbn número d eidentificão do livro
	 * @return um livro com este isbn
	 */
	private Book getABook(String isbn) {
		for(Book item:bookRepository.getAll()) {
			if(item.getIsbn().equals(isbn) && item.isInLibraryPosession()) {
				return item;
			}
		}
		return null;
		
	}

	/**
	 * 
	 * @param idUser id do utilizador
	 * @param isbn número de identificação do livro
	 * @return o utilizador cujo favorito associado ao isbn foi removido
	 */
	@Transactional
	public User removeFavourite(long idUser, String isbn) {
		User user = userRepository.findById(idUser);
		if(user==null) {
			return null;
		}
		if (isfavourite(isbn, user.getFavouriteIsbns())) {
			user.getFavouriteIsbns().remove(isbn);
			userRepository.update(user);
			return userRepository.findById(idUser);
		}
		return null;
	}

	/**
	 * 
	 * @param userEmail email do utilizador
	 * @param userPassword password do utilizador
	 * @return o próprio utilizador caso o userEmail corresponda ao userPassword e null caso
	 * contrário
	 */
	public User loginUser(String userEmail, String userPassword) {
		for (User item : userRepository.getAll()) {
			if (isUserActive(item.getId())) {
				if (item.getEmail() != null && item.getPassword() != null) {
					if (item.getEmail().equals(userEmail) && item.getPassword().equals(userPassword)) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param userId
	 * @param newPassword
	 * @return
	 */
	@Transactional
	public User changeUserPassword(long userId, String newPassword) {
		User userToChange = userRepository.findById(userId);
		if(userToChange==null) {
			return null;
		}
		userToChange.setPassword(newPassword);
		return userRepository.update(userToChange);
	}

	/**
	 * Enviar e-mail
	 * 
	 * @param emailto
	 * @param subject
	 * @param content1
	 */

	/*public void sendEmail(String emailto, String subject, String content1) {
		System.out.println("Entra no send Email"); // verificar emails
		Email from = new Email("geral@biblioteca4.com");
		Email to = new Email(emailto);
		Content content = new Content("text/plain", content1);
		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid("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");
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(response.getStatusCode()); // verificar email status code
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}*/

	/**
	 * 
	 * @param indexToStart índice do ArryList contendo todos os utlizadores
	 * @param amount número de utilizadores a devoler
	 * @return uma lista de composta de amount utilizadores começando no indexToStart
	 */
	public ArrayList<User> getACertainAmountOfUsersFromACertainIndex(int indexToStart, int amount) {
		ArrayList<User> allUsers=new ArrayList<User>();
		ArrayList<User> smallArrayListUsers=new ArrayList<User>();
		allUsers=(ArrayList<User>) userRepository.getAll();
		if(allUsers.isEmpty()) {
			return null;
		}
		if(allUsers.size()>indexToStart+amount-1) {
			for(int i=indexToStart;i<indexToStart+amount;i++) {
				smallArrayListUsers.add(allUsers.get(i));
			}
		} else if(allUsers.size()<=indexToStart+amount-1 && allUsers.size()>indexToStart) {
			for(int i=indexToStart;i<allUsers.size();i++) {
				smallArrayListUsers.add(allUsers.get(i));
			}
		}
		return smallArrayListUsers;
	}
}