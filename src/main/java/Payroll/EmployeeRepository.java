package Payroll;

import org.springframework.data.jpa.repository.JpaRepository;

//This declaration of interface with the types we want to store for each employee is enough to give access to JPA Functionality
interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
