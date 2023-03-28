/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pedantic.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import com.pedantic.config.AbstractEntityListener;
import com.pedantic.config.EmployeeListener;

/**
 * @author Seeraj
 */
@Entity
@NamedQuery(name = Employee.GET_EMPLOYEE_ALLOWANCES, query = "SELECT al FROM Employee e JOIN e.employeeAllowances al WHERE al.allowanceAmount > :greaterThanValue")
@NamedQuery(name = "", query = "SELECT e FROM Employee e WHERE e.basicSalary BETWEEN :lowerBound AND :upperBound")
@NamedQuery(name = "", query = "SELECT e.fullName, KEY(p), VALUE(p) FROM Employee e JOIN e.employeePhoneNumbers p") // Querying Maps
@NamedQuery(name = "", query = "SELECT e FROM Employee e JOIN FETCH e.employeeAllowances")
@NamedQuery(name = Employee.GET_ALL_PARKING_SPACES, query = "SELECT e.parkingSpace FROM Employee e")
@NamedQuery(name = Employee.EMPLOYEE_PROJECTION, query = "SELECT e.fullName, e.basicSalary FROM Employee e") // Will return a collection of Object array
@NamedQuery(name = Employee.EMPLOYEE_CONSTRUCTOR_PROJ, query = "SELECT new com.pedantic.entities.EmployeeDetails(e.fullName, e.basicSalary, e.department.departmentName) from Employee e")
@NamedQuery(name = Employee.FIND_BY_ID, query = "select e from Employee e where e.id = :id and e.userEmail = :email")
@NamedQuery(name = Employee.FIND_BY_NAME, query = "select e from Employee e where e.fullName = :name and e.userEmail = :email")
@NamedQuery(name = Employee.LIST_EMPLOYEES, query = "select  e from Employee e where e.userEmail = :email order by e.fullName")
@NamedQuery(name = Employee.FIND_PAST_PAYSLIP_BY_ID, query = "select p from Employee e join e.pastPayslips p where e.id = :employeeId and e.userEmail =:email and p.id =:payslipId and p.userEmail = :email")
@NamedQuery(name = Employee.GET_PAST_PAYSLIPS, query = "select p from Employee e inner join e.pastPayslips p where e.id = :employeeId and e.userEmail=:email")
//@Table(name = "Employee", schema = "HR")
@EntityListeners({ EmployeeListener.class, AbstractEntityListener.class })
public class Employee extends AbstractEntity {
//public class Employee {

//    Create a table containing primary key generated. This is the most flexible.
//    @TableGenerator(name = "Emp_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE")
//    @GeneratedValue(generator = "Emp_Gen")
//    @Id
//    private Long id;

	public static final String EMPLOYEE_SALARY_BOUND = "Employee.salaryBound";
	public static final String EMPLOYEE_PROJECTION = "Employee.nameAndSalaryProjection";
	public static final String EMPLOYEE_CONSTRUCTOR_PROJ = "Employee.projection";
	public static final String GET_EMPLOYEE_ALLOWANCES = "Employee.getAllowances";
	public static final String FIND_BY_ID = "Employee.findById";
	public static final String FIND_BY_NAME = "Employee.findByName";
	public static final String LIST_EMPLOYEES = "Employee.listEmployees";
	public static final String FIND_PAST_PAYSLIP_BY_ID = "Employee.findPastPayslipById";
	public static final String GET_PAST_PAYSLIPS = "Employee.getPastPayslips";
	public static final String GET_ALL_PARKING_SPACES = "Employee.getAllParkingSpaces";

	@NotEmpty(message = "Name cannot be empty")
	@Size(max = 40, message = "Full name must be less than 40 characters")
	@Basic
	private String fullName;

	@Past(message = "Date of birth must be in the past")
	@JsonbDateFormat(value = "yyyy-MM-dd")
	private LocalDate dateOfBirth; // yyyy-MM-dd

	@NotNull(message = "Basic salary must be set")
	@DecimalMin(value = "500", message = "Basic salary must be equal to or exceed 500")
	private BigDecimal basicSalary;

	@NotNull(message = "Hired date must be set")
	@JsonbDateFormat(value = "yyyy-MM-dd")
	@PastOrPresent(message = "Hired date must be in the past or present")
	private LocalDate hiredDate;

	@ManyToOne
	private Employee reportsTo;

	@OneToMany
	private Set<Employee> subordinates = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private EmploymentType employmentType;

	@Embedded
	private Address address;

	@ElementCollection // maps collections of Elements (includes Embeddable classes)
	@CollectionTable( // customize the Collection Table
			name = "QUALIFICATIONS", joinColumns = @JoinColumn(name = "EMP_ID"))
	private Collection<Qualifications> qualifications;

	@ElementCollection
	@Column(name = "NICKY")
	private Collection<String> nickNames;

	@DecimalMax(value = "60", message = "Age must not exceed 60")
	private int age;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }) // Allowance will be saved and removed along
																		// employee
	private Set<Allowance> employeeAllowances = new HashSet<>();

	@OneToOne // Unidirectional to Payslip
	@JoinColumn(name = "CURRENT_PAYSLIP_ID") // // Foreign key column will be named to 'CURRENT_PAYSLIP_ID'
	private Payslip currentPayslip;

	@OneToOne(mappedBy = "employee", // Bidirectional to ParkingSpace; mappedBy specifies that the entity on the
										// other side is the owning
			fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) // Saving an employee also saves a parkingSpace
	private ParkingSpace parkingSpace;

	@OneToMany
	private Collection<Payslip> pastPayslips = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "EMP_PHONE_NUMBERS")
	@MapKeyColumn(name = "PHONE_TYPE") // Key
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "PHONE_NUMBER") // Value
	private Map<PhoneType, String> employeePhoneNumbers = new HashMap<>();

	@ManyToOne
	@JoinColumn(name = "DEPT_ID") // Foreign key column will be named to 'DEPT_ID'
	private Department department;

	@ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
	private Collection<Project> projects = new ArrayList<>();

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] picture;

//    @PrePersist // invoked before an entity is saved
//    @PostPersist // does not necessary mean that a transaction is committed
//    @PreUpdate // invoked just before an entity is updated
//    @PostUpdate // invoked after an entity is updated
//    @PostLoad // invoked after data has been loaded
//    private void init() {
//        this.age = Period.between(dateOfBirth, LocalDate.now()).getYears();
//    }

	public Employee getReportsTo() {
		return reportsTo;
	}

	public void setReportsTo(Employee reportsTo) {
		this.reportsTo = reportsTo;
	}

	public Set<Employee> getSubordinates() {
		return subordinates;
	}

	public void setSubordinates(Set<Employee> subordinates) {
		this.subordinates = subordinates;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public EmploymentType getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(EmploymentType employmentType) {
		this.employmentType = employmentType;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public Payslip getCurrentPayslip() {
		return currentPayslip;
	}

	public void setCurrentPayslip(Payslip currentPayslip) {
		this.currentPayslip = currentPayslip;
	}

	public Collection<Payslip> getPastPayslips() {
		return pastPayslips;
	}

	public void setPastPayslips(Collection<Payslip> pastPayslips) {
		this.pastPayslips = pastPayslips;
	}

	public LocalDate getHiredDate() {
		return hiredDate;
	}

	public void setHiredDate(LocalDate hiredDate) {
		this.hiredDate = hiredDate;
	}

	public Set<Allowance> getEmployeeAllowances() {
		return employeeAllowances;
	}

	public void setEmployeeAllowances(Set<Allowance> employeeAllowances) {
		this.employeeAllowances = employeeAllowances;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public BigDecimal getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(BigDecimal basicSalary) {
		this.basicSalary = basicSalary;
	}

	public Collection<Qualifications> getQualifications() {
		return qualifications;
	}

	public void setQualifications(Collection<Qualifications> qualifications) {
		this.qualifications = qualifications;
	}

	public Collection<String> getNickNames() {
		return nickNames;
	}

	public void setNickNames(Collection<String> nickNames) {
		this.nickNames = nickNames;
	}

	public ParkingSpace getParkingSpace() {
		return parkingSpace;
	}

	public void setParkingSpace(ParkingSpace parkingSpace) {
		this.parkingSpace = parkingSpace;
	}

	public Map<PhoneType, String> getEmployeePhoneNumbers() {
		return employeePhoneNumbers;
	}

	public void setEmployeePhoneNumbers(Map<PhoneType, String> employeePhoneNumbers) {
		this.employeePhoneNumbers = employeePhoneNumbers;
	}

	public Collection<Project> getProjects() {
		return projects;
	}

	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
