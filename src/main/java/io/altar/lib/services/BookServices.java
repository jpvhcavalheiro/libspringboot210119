package io.altar.lib.services;

import java.util.ArrayList;

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

import io.altar.lib.DTOs.BookDTO;
import io.altar.lib.business.BookBusiness;
import io.altar.lib.model.*;


@Component
@Path("/libraryManagmentApp/api/books")
public class BookServices {

	@Inject
	BookBusiness bookBusiness;
	
	//VISTO
	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public String verify(){
		return "Server books OK Ola!";
	}
	
	//VISTO
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Book createBook(Book bookToSave){
		return bookBusiness.createBook(bookToSave);
	}
	//VISTO
	@ GET
	@ Path("/{id}")
	@ Produces(MediaType.APPLICATION_JSON)
	public Book getABook(@PathParam("id") long id){
		return bookBusiness.getABook(id);
	}
	
	//VISTO
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Book> seeAllBooks(){
		return bookBusiness.seeAllBooks();
	}
	
	//VISTO
	@PUT
	@Path("removebook/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Book removeBook(@PathParam("id") long id){
		return bookBusiness.removeBook(id);
	}
	
	//VISTO
	@PUT
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Book updateBook(Book updatedBook){
		
		return bookBusiness.updateBook(updatedBook);
	}
	
	//VISTO
	@GET
	@Path("/generalresearch")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> generalResearchForBook(@NotNull @QueryParam("keyExpression") String keyExpression){
		return bookBusiness.generalResearchForBook(keyExpression);
	}
	
	//VISTO
	@GET
	@Path("/researchbytitle")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> researchBookByTitle(@NotNull @QueryParam("titleToTest") String titleToTest){
		return bookBusiness.researchBookByTitle(titleToTest);
	}
	
	//VISTO
	@GET
	@Path("/researchbydescription")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> researchBookByDescription(@NotNull @QueryParam("descriptionToTest") String descriptionToTest){
		return bookBusiness.researchBookByDescription(descriptionToTest);
	}
	
	//VISTO
	@GET
	@Path("/researchbyauthor")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> researchBookByAuthor(@NotNull @QueryParam("authorToTest") String authorToTest){
		return bookBusiness.researchBookByAuthor(authorToTest);
	}
	
	//VISTO
	@GET
	@Path("/researchbytopic")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> researchBookByTopic(@NotNull @QueryParam("topicToTest") String topicToTest){
		return bookBusiness.researchBookByTopic(topicToTest);
	}
	
	//VISTO
	@GET
	@Path("/getallavailablebooks")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Book> getAllAvailableBooks(){
		return bookBusiness.getAllAvailableBooks();
	}
	
	//VISTO
	@GET
	@Path("/researchbyisbn")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Book> researchBookByISBN(@NotNull @QueryParam("isbnToTest") String isbnToTest){
		return bookBusiness.researchBookByISBN(isbnToTest);
	}

	//VISTO
	@GET
	@Path("/getallbooksbyisbn")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> getAllBooksByISbnSpecial() {
		return bookBusiness.getAllBooksByISbnSpecial();
	}
	
	//VISTO
	@GET
	@Path("getacertainamountofbooksdto")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookDTO> getACertainAmountOfBookDTOFromACertainIndex(@NotNull @QueryParam("indextostart") int indexToStart, @NotNull @QueryParam("amount") int amount){
		return bookBusiness.getACertainAmountOfBookDTOFromACertainIndex(indexToStart,amount);
	}
}
