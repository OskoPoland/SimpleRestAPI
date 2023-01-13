package Payroll;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
class Employee {
    //Auto generates a primary key for the database
    private @Id @GeneratedValue long id;
    private String firstname;
    private String lastname;
    private String role;

    public Employee() {}

    public Employee(String firstname,String lastname, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    //Employee info getters
    public long getId() {return this.id;}
    public String getName() {return this.firstname + " " + this.lastname;}
    public String getFirstname() {return this.firstname;}
    public String getLastname() {return this.lastname;}
    public String getRole() {return this.role;}

    //Employee info setters
    public void setId(long id) {this.id = id;}
    public void setName(String name) {
        String[] split = name.split(" ");
        this.firstname = split[0];
        this.lastname = split[1];
    }
    public void setFirstname(String fn) {this.firstname = fn;}
    public void setLastname(String ln) {this.lastname = ln;}
    public void setRole(String role) {this.role = role;}

    @Override
    public boolean equals (Object o) {
        //if we are comparing it with itself
        if (this == o) return true;

        //if we are comparing it with a different object
        if (!(o instanceof Employee)) return false;

        //return true if objects name & id are the same
        Employee employee = (Employee) o;
        return Objects.equals(this.id, employee.id) && Objects.equals(this.firstname, employee.firstname) &&
                Objects.equals(this.lastname, employee.lastname) &&
                Objects.equals(this.role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.firstname, this.lastname, this.role);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id + ", firstName='" + this.firstname + '\'' + ", lastName='" + this.lastname
                + '\'' + ", role='" + this.role + '\'' + '}';
    }
}