package com.pedantic.service;

import com.pedantic.entities.Department;
import com.pedantic.entities.Employee;
import com.pedantic.entities.ParkingSpace;
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
}
