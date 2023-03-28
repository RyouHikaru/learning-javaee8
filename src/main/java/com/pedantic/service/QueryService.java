package com.pedantic.service;

import com.pedantic.entities.Allowance;
import com.pedantic.entities.Department;
import com.pedantic.entities.Employee;
import com.pedantic.entities.EmployeeDetails;
import com.pedantic.entities.ParkingSpace;
import com.pedantic.entities.Project;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless // does not keep state; used for independent operations; goes back to bean pool to serve other clients
public class QueryService {
    
    // This entityManager is injected from Producers
    @Inject
    EntityManager entityManager;
    
    @PostConstruct // invoked after all beans are created
    private void init() {
        
    }
    
    @PreDestroy // invoked before all beans are destroyed
    private void destroy() {
        
    }
    
    public List<Department> getAllDepartments() {
        return entityManager.createNamedQuery(Department.GET_DEPARTMENT_LIST, Department.class).getResultList();
    }
    
    public List<String> getAllDepartmentNames() {
        return entityManager.createNamedQuery(Department.GET_DEPARTMENT_NAMES, String.class).getResultList();
    }
    
    public List<ParkingSpace> getAllAllocatedParkingSpaces() {
        return entityManager.createNamedQuery(Employee.GET_ALL_PARKING_SPACES, ParkingSpace.class).getResultList();
    }
    
    public Collection<Object[]> getEmployeeProjection() {
        return entityManager.createNamedQuery(Employee.EMPLOYEE_PROJECTION, Object[].class).getResultList();
    }
    
    public List<EmployeeDetails> getEmployeeDetails() {
        return entityManager.createNamedQuery(Employee.EMPLOYEE_CONSTRUCTOR_PROJ, EmployeeDetails.class).getResultList();
    }
    
    public Collection<Allowance> getEmployeeAllowances(BigDecimal greaterThanValue) {
        return entityManager.createNamedQuery(Employee.GET_EMPLOYEE_ALLOWANCES, Allowance.class)
                .setParameter("greaterThanValue", greaterThanValue).getResultList();
    }
    
    public Collection<Employee> filterEmployeesBySalary(BigDecimal lowerBound, BigDecimal upperBound) {
        return entityManager.createNamedQuery(Employee.EMPLOYEE_SALARY_BOUND, Employee.class)
                .setParameter("upperBound", upperBound)
                .setParameter("lowerBound", lowerBound)
                .getResultList();
    }
    
    public Collection<Employee> filterEmployeesByName(String pattern) {
        TypedQuery<Employee> filter =  entityManager.createQuery("SELECT e FROM Employee e WHERE e.fullName LIKE :filter", Employee.class)
                .setParameter("filter", "%" + pattern + "%");
        
        return filter.getResultList();
    }
    
    public Employee getEmployeeWithHighestSalary() {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.basicSalary = (SELECT MAX(emp.basicSalary) FROM Employee emp)", 
                Employee.class).getSingleResult(); // Use only if there is a certain Single result
    }
    
    public Collection<Employee> filterEmployeesByState() {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.address.state IN ('NY', 'CA')", Employee.class)
                .getResultList();
    }
    
    public Collection<Employee> getManagers() {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.subordinates IS NOT EMPTY", Employee.class)
                .getResultList();
    }
    
    public Collection<Employee> getEmployeesByProject(Project project) {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE :project MEMBER OF e.projects ORDER BY e.department.departmentName", Employee.class)
                .setParameter("project", project).getResultList();
    }
    
    public Collection<Employee> getAllLowerPaidManagers() {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.subordinates IS NOT EMPTY AND e.basicSalary < ALL (SELECT s.basicSalary FROM e.subordinates s)", Employee.class)
                .getResultList();
    }
    
    public Collection<Employee> getEmployeesBonus() {
        return entityManager.createQuery("SELECT e, e.basicSalary * 0.15 AS bonus FROM Employee e ORDER BY bonus", Employee.class)
                .getResultList();
    }
    
    public Collection<Object[]> getTotalEmployeeSalariesByDept() {
        TypedQuery<Object[]> query = entityManager.createNamedQuery("SELECT d.departmentName, SUM(e.basicSalary) FROM Department d JOIN d.employees e GROUP BY d.departmentName", Object[].class);
        
        return query.getResultList();
    }
    
    public Collection<Object[]> getAverageEmployeeSalaryByDept() {
        return entityManager.createNamedQuery("SELECT d.departmentName, AVG(e.basicSalary) FROM Department d JOIN d.employees e WHERE e.subordinates IS EMPTY GROUP BY d.departmentName", Object[].class)
                .getResultList();
    }
    
    public Collection<Object[]> getAverageEmployeeSalaryByDept(BigDecimal minimumThreshold) {
        return entityManager.createNamedQuery("SELECT d.departmentName, AVG(e.basicSalary) FROM Department d JOIN d.employees e WHERE e.subordinates IS EMPTY GROUP BY d.departmentName HAVING AVG(e.basicSalary) > :minThreshold", Object[].class)
                .setParameter("minThreshold", minimumThreshold)
                .getResultList();
    }
    
    public Collection<Object[]> countEmployeesByDept() {
        return entityManager.createNamedQuery("SELECT d.departmentName, COUNT(e) FROM Department d JOIN d.employees e GROUP BY d.departmentName", Object[].class)
                .getResultList();
    }
    
    public Collection<Object[]> getEmployeesLowestByDept() {
        return entityManager.createNamedQuery("SELECT d.departmentName, MAX(e.basicSalary) FROM Department d JOIN d.employees e GROUP BY d.departmentName", Object[].class)
                .getResultList();
    }
    
    public Department findDepartmentById(Long id) {
        return entityManager.find(Department.class, id); // find an instance of the entity Department
    }
    
    public Employee findEmployeeById(Long id) {
        return entityManager.find(Employee.class, id); // find an instance of the entity Employee
    }
    
    public List<Employee> getEmployees() {
        return null;
    }
    
    public List<Department> getDeparments() {
        return null;
    }
    
    public Collection<Employee> bla() {
//        SELECT e FROM Employee e WHERE e.fullName = 'Average Joe'
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
        Root<Employee> emp = c.from(Employee.class);
        CriteriaQuery<Employee> query = c.select(emp)
                .where(cb.equal(emp.get("fullName"), "Average Joe"));
        
        return entityManager.createQuery(query).getResultList();
    }
}
