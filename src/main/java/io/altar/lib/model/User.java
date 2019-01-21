package io.altar.lib.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u")
public class User extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String name;
	private String nip;
	private String password;
	private boolean admin = false;
	private ArrayList<String> favouriteIsbns;
	private boolean active=true;


	public User(){}


	public User(String email, String name, String nip, String password, boolean admin, ArrayList<String> favouriteIsbns,
			boolean active) {
		super();
		this.email = email;
		this.name = name;
		this.nip = nip;
		this.password = password;
		this.admin = admin;
		this.favouriteIsbns = favouriteIsbns;
		this.active = active;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getNip() {
		return nip;
	}


	public void setNip(String nip) {
		this.nip = nip;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isAdmin() {
		return admin;
	}


	public void setAdmin(boolean admin) {
		this.admin = admin;
	}


	public ArrayList<String> getFavouriteIsbns() {
		return favouriteIsbns;
	}


	public void setFavouriteIsbns(ArrayList<String> favouriteIsbns) {
		this.favouriteIsbns = favouriteIsbns;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	@Override
	public String toString() {
		return "User [email=" + email + ", name=" + name + ", nip=" + nip + ", password=" + password + ", admin="
				+ admin + ", favouriteIsbns=" + favouriteIsbns + ", active=" + active + "]";
	}


	

	
	

	

}