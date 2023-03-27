package com.pedantic.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Defaulting to the persistence provider (IDENTITY is also available)
    protected Long id;

//    Primitive Java types: byte, int, short, long, and char
//    Wrapper classes of primitive Java types: Byte, Integer, Short, Long,
//    and Character
//    String: java.lang.String
//    Large numeric type: java.math.BigInteger
//    Temporal types: java.util.Date and java.sql.Date

    protected String userEmail;

    @Version
    protected Long version;


    protected LocalDateTime createdOn;
    protected LocalDateTime updatedOn;

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
