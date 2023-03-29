package com.pedantic.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
import javax.ws.rs.FormParam;

@Entity
@NamedQuery(name = ApplicationUser.FIND_USER_BY_CREDENTIALS, query = "select u from ApplicationUser  u where  u.email = :email")
public class ApplicationUser extends AbstractEntity{

    public static final String FIND_USER_BY_CREDENTIALS = "User.findUserByCredentials";
    
//    @SequenceGenerator(name = "User_seq", sequenceName = "User_sequence")
//    @GeneratedValue(generator = "User_seq")
//    @Id
//    private Long id;

//    CREATE SEQUENCE Emp_Seq
//    MINVALUE 1
//    START WITH 1
//    INCREMENT BY 50

    @NotEmpty(message = "Email must be set")
    @Email(message = "The email must be in the form user@domain.com")
//    @FormParam("email")
    private String email;

    @NotEmpty(message = "Password must be set")
    @Size(min = 8)
//    @Pattern(regexp = "", message = "Password must be in the form....")
//    @FormParam("password")
    private String password;

    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

	@Override
	public String toString() {
		return "ApplicationUser [email=" + email + ", password=" + password + "]";
	}
    
}
