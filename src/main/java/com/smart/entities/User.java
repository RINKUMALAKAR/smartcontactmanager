package com.smart.entities;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity

public class User {
	@Id
    @GeneratedValue(strategy =GenerationType.AUTO)
	private int id;
	
   @NotBlank(message="Name field is required")
   @Size(min=2, max =20)
   private String name;
   @Size(min=8 , message="Passwod Length Should be greater or equal to 8")
   private String password;
   @Column(unique =true)
   @Email(message = "Email is not valid")
    @NotEmpty(message = "Email cannot be empty")
   //@NotBlank(message = "Email field is required")
   private String email;
   private String role;
   private String imageUrl;
   @Column(length= 500)
   private String about;
   private boolean enabled;
   
   @OneToMany(cascade =CascadeType.ALL, fetch=FetchType.LAZY ,mappedBy ="user" )
   @JsonIgnore
   private List<Contact> contactList = new ArrayList<>();

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

public String getImageUrl() {
	return imageUrl;
}

public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}

public String getAbout() {
	return about;
}

public void setAbout(String about) {
	this.about = about;
}

public boolean isEnabled() {
	return enabled;
}

public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}

public List<Contact> getContactList() {
	return contactList;
}

public void setContactList(List<Contact> contactList) {
	this.contactList = contactList;
}

@Override
public String toString() {
	return "User [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email + ", role=" + role
			+ ", imageUrl=" + imageUrl + ", about=" + about + ", enabled=" + enabled + ", contactList=" + contactList
			+ "]";
}

   
}
