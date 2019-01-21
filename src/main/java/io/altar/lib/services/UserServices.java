package io.altar.lib.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.altar.lib.business.UserBusiness;
import io.altar.lib.model.*;

@Component
@Path("/libraryManagmentApp/api/users")
public class UserServices {
	@Inject
	UserBusiness userBusiness;

	//Testado
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User createUser(User input) {
		return userBusiness.createUser(input);
	}

	//Testado
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("userId") long userId) {
		return userBusiness.getUser(userId);
	}

	//Testado
	@PUT
	@Path("/disable/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public User disableUser(@PathParam("userId") long userId) {
		return userBusiness.changeUserActiveState(userId, false);
	}

	//Testado
	@PUT
	@Path("/reactivateuser/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public User reactivateUser(@PathParam("userId") long userId) {
		return userBusiness.changeUserActiveState(userId, true);
	}

	//Testado
	@PUT
	@Path("/update/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User updateUser(@PathParam("userId") long userId, User input) {
		return userBusiness.updateUser(input,userId);
	}

	//Testado
	@GET
	@Path("/getall")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllUsers() {
		return userBusiness.getAll();
	}

	//Testado
	@GET
	@Path("/findby/name")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> searchUserByName(@NotNull @QueryParam("name") String name) {
		return userBusiness.searchUserByName(name);
	}

	//Testado
	@GET
	@Path("/findby/nip")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> searchUserByNip(@NotNull @QueryParam("nip") String nip) {
		return userBusiness.searchUserByNip(nip);
	}

	//Testado
	@GET
	@Path("/findby/email")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> searchUserByEmail(@NotNull @QueryParam("email") String email) {
		return userBusiness.searchUserByEmail(email);
	}

	//Testado
	@PUT
	@Path("/changetoadmin/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public User turnUserToAdmin(@PathParam("userId") long userId) {
		return userBusiness.turnUserToAdmin(userId);
	}

	//Testado
	@POST
	@Path("/addfavourite/")
	@Produces(MediaType.APPLICATION_JSON)
	public User addBookToFavourites(@NotNull @QueryParam("userId") long userId,
			@NotNull @QueryParam("bookIsbn") String bookIsbn) {
		return userBusiness.addBookToFavourites(userId, bookIsbn);
	}

	//Testado
	@GET
	@Path("/getallfavourites/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Book> getAllFavourites(@PathParam("userId") long userId) {
		return userBusiness.getAllFavourites(userId);
	}

	//Testado
	@DELETE
	@Path("/removefavourite/")
	@Produces(MediaType.APPLICATION_JSON)
	public User removeFavourite(@NotNull @QueryParam("userId") long userId, @NotNull @QueryParam("isbn") String isbn) {
		return userBusiness.removeFavourite(userId, isbn);
	}

	//Testado
	@GET
	@Path("/loginuser")
	@Produces(MediaType.APPLICATION_JSON)
	public User loginUser(@NotNull @QueryParam("userEmail") String userEmail,
			@NotNull @QueryParam("userPassword") String userPassword) {
		return userBusiness.loginUser(userEmail, userPassword);
	}

	//Testado
	@PUT
	@Path("/changepassword/{userId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public User changeUserPassword(@PathParam("userId") long userId, String newPassword) {
		return userBusiness.changeUserPassword(userId, newPassword);
	}

	//Testado
	@GET
	@Path("getacertainamountofusers")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getACertainAmountOfUsersFromACertainIndex(
			@NotNull @QueryParam("indextostart") int indexToStart, @NotNull @QueryParam("amount") int amount) {
		return userBusiness.getACertainAmountOfUsersFromACertainIndex(indexToStart, amount);
	}

}