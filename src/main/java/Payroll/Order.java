package Payroll;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
//generates table named customer order
@Table(name = "CUSTOMER_ORDER")
public class Order {
    private @Id @GeneratedValue long id;
    private String description;
    private Status status;

    public Order() {}

    public Order(String description, Status status) {
        this.description = description;
        this.status = status;
    }

    //getters
    public long getId() {return this.id;}
    public String getDescription() {return this.description;}
    public Status getStatus() {return this.status;}

    //setters
    public void setId(long id) {this.id = id;}
    public void setDescription(String description) {this.description = description;}
    public void setStatus(Status status) {this.status = status;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(this.id, order.id) && Objects.equals(this.description, order.description)
                && Objects.equals(this.status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.description, this.status);
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + this.id + ", description='" + this.description + '\'' + ", status=" + this.status + '}';
    }
}
