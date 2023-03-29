package com.pedantic.entities;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
import javax.ws.rs.FormParam;

@Entity
public class ApplicationUser extends AbstractEntity{
//public class ApplicationUser {
    
//    Create a Sequence
//    @SequenceGenerator(name = "User_seq", sequenceName = "User_sequence")
//    @GeneratedValue(generator = "User_seq")
//    @Id
//    private Long id;

	@NotEmpty(message = "Email must be set")
	@Email(message = "The email must be in the form user@domain.com")
	@FormParam("email") // Used in BeanParam
    private String email;
	
	@NotEmpty(message = "Password must be set")
	@Size(min = 8, max = 100)
	@FormParam("password") // Used in BeanParam
//	@Pattern() for Regex
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
