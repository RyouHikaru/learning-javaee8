package com.pedantic.entities;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.pedantic.config.AbstractEntityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@NamedQuery(name = Department.GET_DEPARTMENT_LIST,  query = "SELECT d FROM Department d") // Compiled together with classes and can be optimized
@NamedQuery(name = Department.GET_DEPARTMENT_NAMES, query = "SELECT d.departmentName FROM Department d")
@NamedQuery(name = Department.FIND_BY_ID, query = "select d from Department d where d.id = :id and d.userEmail = :email")
@NamedQuery(name = Department.FIND_BY_NAME, query = "select d from Department d where d.departmentName = :name and d.userEmail = :email")
@NamedQuery(name = Department.LIST_DEPARTMENTS, query = "select d from Department d where  d.userEmail = :email")
@Access(AccessType.FIELD)
@EntityListeners({ AbstractEntityListener.class })
public class Department extends AbstractEntity {

    public static final String FIND_BY_ID = "Department.findById";
    public static final String FIND_BY_NAME = "Department.findByName";
    public static final String LIST_DEPARTMENTS = "Department.listDepartments";
    public static final String GET_DEPARTMENT_LIST = "Department.getAllDepartments";
    public static final String GET_DEPARTMENT_NAMES = "Department.getDeptNames";

    @NotEmpty(message = "Department name must be set")
    @Pattern(regexp = "", message = "Department name must be in the form Dept Abbreviation, Number, and Branch")
    private String departmentName;

//    @OneToMany(mappedBy = "department") // The other entity has the Foreign key column
//    @OrderBy("fullName ASC, dateOfBirth DESC") // Employees will be sorted by the criteria
//    @OrderColumn(name = "EMPLOYEE_POSITION") // A column will be created in DB to store the Order
//    private List<Employee> employees = new ArrayList<>();
    
    @OneToMany
    @MapKey(name = "id") // Key of Basic Type
    @JoinTable(name = "DEPT_EMPLOYEES")
    private Map<Long, Employee> employees = new HashMap<>();

    @ElementCollection // Key of Entity
    @CollectionTable(name = "EMPLOYEE_RANKS")
    @MapKeyJoinColumn(name = "EMP_ID")
    @Column(name = "RANK")
    private Map<Employee, Integer> employeeRanks = new HashMap<>();
    
    @Transient
    private String departmentCode;


    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Map<Long, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<Long, Employee> employees) {
        this.employees = employees;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		return Objects.equals(getDepartmentName().toUpperCase(), other.departmentName.toUpperCase());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDepartmentName().toUpperCase());
	}

    
    
}
