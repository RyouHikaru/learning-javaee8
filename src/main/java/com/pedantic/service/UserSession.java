package com.pedantic.service;

import java.io.Serializable;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;

@Stateful // keeps state; serves only one client and new instance is created every time needed
public class UserSession implements Serializable {
    
    public String getCurrentUserName() {
        return "";
    }
    
    // also has PostConstruct and PreDestroy
    
    @PrePassivate
    private void passivate() {
        
    }
    
    @PostActivate
    private void active() {
        
    }
}
