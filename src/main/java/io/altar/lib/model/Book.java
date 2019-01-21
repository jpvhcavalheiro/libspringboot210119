package io.altar.lib.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
@NamedQuery(name = Book.GET_ALL_BOOKS_QUERY_NAME, query = "SELECT b FROM Book b")
@Entity
@Table(name = "Books")
public class Book extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public static final String GET_ALL_BOOKS_QUERY_NAME = "getAllBooks";
//	public static final String GET_ALL_DIFFERENT_ISBNS = "getAllDifferentIsbns";

	private String title;
	@Lob
	private String description;
	private String author;
	private String photoLink;
	private String topic;
	private String location;
	private String isbn;
	private String state;
	private boolean inLibraryPosession;
	private double rating;

	

	public Book(String title, String description, String author, String photoLink, String topic, String location,
			String isbn, String state, boolean inLibraryPosession, double rating) {
		super();
		this.title = title;
		this.description = description;
		this.author = author;
		this.photoLink = photoLink;
		this.topic = topic;
		this.location = location;
		this.isbn = isbn;
		this.state = state;
		this.inLibraryPosession = inLibraryPosession;
		this.rating = rating;
	}

	public Book() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPhotoLink() {
		return photoLink;
	}

	public void setPhotoLink(String photoLink) {
		this.photoLink = photoLink;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isInLibraryPosession() {
		return inLibraryPosession;
	}

	public void setInLibraryPosession(boolean inLibraryPosession) {
		this.inLibraryPosession = inLibraryPosession;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "Book [title=" + title + ", description=" + description + ", author=" + author + ", photoLink="
				+ photoLink + ", topic=" + topic + ", location=" + location + ", isbn=" + isbn + ", state=" + state
				+ ", inLibraryPosession=" + inLibraryPosession + ", rating=" + rating + "]";
	}

	

}
