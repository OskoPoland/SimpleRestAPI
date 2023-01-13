package Payroll;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(long id) {
        super("Order: " + id + " cannot be found");
    }
}
