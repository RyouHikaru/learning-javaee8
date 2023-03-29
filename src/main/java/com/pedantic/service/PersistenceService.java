package com.pedantic.service;

import com.pedantic.entities.ApplicationUser;
import com.pedantic.entities.Department;
import com.pedantic.entities.Employee;
import com.pedantic.entities.ParkingSpace;

import java.util.Map;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;


@DataSourceDefinition(
        name = "java:app/Payroll/MyDS",
        className = "org.apache.derby.jdbc.ClientDriver",
        url = "jdbc:derby://localhost:1527/payroll",
        user = "appuser",
        password = "password")
@Stateless
public class PersistenceService {

    @Inject
    EntityManager entityManager;
    
    @Inject
    QueryService queryService;

    @Inject
    SecurityUtil securityUtil;
    
    public void saveDepartment(Department department) {
        entityManager.persist(department); // creates new instance of this entity in the DB
    }
    
    public void updateDepartment(Department department) {
        entityManager.merge(department); // merges back a detached instance to the persistence context
    }
    
    public void removeParkingSpace(Long employeeId) {
        Employee employee = queryService.findEmployeeById(employeeId);
        ParkingSpace parkingSpace = employee.getParkingSpace();
        
        employee.setParkingSpace(null); // sets first the current Employee's parkingSpace to null before removing
        
        entityManager.remove(parkingSpace); // remove after updating employee's parkingSpace
    }
    
    public void saveEmployee(Employee employee, ParkingSpace parkingSpace) {
        employee.setParkingSpace(parkingSpace); // has Cascade PERSIST
        entityManager.persist(employee);
    }
    
    public void saveEmployee(Employee employee) {
    	if (employee.getId() == null) {
    		entityManager.persist(employee);
    	} else {
        	entityManager.merge(employee);
    	}
    }

    public void saveUser(ApplicationUser applicationUser) {

        Map<String, String> credMap = securityUtil.hashPassword(applicationUser.getPassword());

        applicationUser.setPassword(credMap.get("hashedPassword"));
        applicationUser.setSalt(credMap.get("salt"));


        if (applicationUser.getId() == null) {
            entityManager.persist(applicationUser);
        } else {
            entityManager.merge(applicationUser);
        }

        credMap = null;

    }
}
