package io.altar.lib.services;



import java.util.ArrayList;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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

import io.altar.lib.DTOs.BookDTO;
import io.altar.lib.business.HistoryBusiness;
import io.altar.lib.model.*;

@Component
@Path("/libraryManagmentApp/api/historys")
public class HistoryServices {
	@Inject
	HistoryBusiness historyBusiness;
	
	
	//Testado
	@POST
	@Path("/reservebook")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public History reserveBookHistory(History newHistory){
		return historyBusiness.reserveBookHistory(newHistory);
	}
	
	//Testado
	@PUT
	@Path("/pickupbook")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public History pickUpBook(Book bookToPickUp){
		return historyBusiness.pickUpBook(bookToPickUp);
	}
	
	//Testado
	@PUT
	@Path("/deliverbook")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public History deliverBook(Book bookToDeliver){
		return historyBusiness.deliverBook(bookToDeliver);
	}
	
	//Testado
	@GET
	@Path("/getuserwithbook/{idBook}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserWithBook(@PathParam("idBook") long idBook){
		return historyBusiness.getUserWithBook(idBook);
	}
	
	//Testado
	@GET
	@Path("/bookinusebyuser/{idUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<History> getBooksWithUser(@PathParam("idUser") long idUser){
		System.out.println("entrou no endpoint");
		return historyBusiness.getBooksWithUser(idUser);
	}
	
	//Testado
	@GET
	@Path("/userhistory/{idUser}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<History> getAllHstoryOfUser(@PathParam("idUser") long idUser){
		return historyBusiness.getAllHstoryOfUser(idUser);
	}
	
	//Testado
	@PUT
	@Path("/cancelreservation")
	@Produces(MediaType.APPLICATION_JSON)
	public History cancelRservation(@NotNull @QueryParam("userId") long userId,@NotNull @QueryParam("bookId") long bookId){
		return historyBusiness.cancelReservation(userId,bookId);
	}
	
	//Testado
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<History> getAllBooks(){
		return historyBusiness.getAllHistorys();
	}
	
	//Testado
	@GET
	@Path("/getfivemostreadbooks")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> getFiveMostReadIsbns(){
		return historyBusiness.getFiveMostReadIsbns();
	}
	
	//Testado
	@GET
	@Path("getacertainamountofhistorys")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<History> getACertainAmountOfHistorysFromACertainIndex(@NotNull @QueryParam("indextostart") int indexToStart, @NotNull @QueryParam("amount") int amount){
		return historyBusiness.getACertainAmountOfHistorysFromACertainIndex(indexToStart,amount);
	}
}
