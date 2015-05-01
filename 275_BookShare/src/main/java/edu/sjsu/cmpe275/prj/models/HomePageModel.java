package edu.sjsu.cmpe275.prj.models;

import org.springframework.stereotype.Repository;

/**
 * This model class contains the properties for the filed on the login.jsp page.
 * @author karan
 *
 */

@SuppressWarnings("unused")
public class HomePageModel {
 
	
	 private String id;
 
   
    private String firstname;
    

    private String lastname;
    
    
    private String email;
    
    private String address;
    
    private String organization;
    
    private String aboutMyself;
    
   





	public String getId() {
		return id;
	}





	public void setId(String id) {
		this.id = id;
	}





	public String getFirstname() {
		return firstname;
	}





	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}





	public String getLastname() {
		return lastname;
	}





	public void setLastname(String lastname) {
		this.lastname = lastname;
	}





	public String getEmail() {
		return email;
	}





	public void setEmail(String email) {
		this.email = email;
	}





	public String getAddress() {
		return address;
	}





	public void setAddress(String address) {
		this.address = address;
	}





	public String getOrganization() {
		return organization;
	}





	public void setOrganization(String organization) {
		this.organization = organization;
	}





	public String getAboutMyself() {
		return aboutMyself;
	}





	public void setAboutMyself(String aboutMyself) {
		this.aboutMyself = aboutMyself;
	}









	
    
   
    
}