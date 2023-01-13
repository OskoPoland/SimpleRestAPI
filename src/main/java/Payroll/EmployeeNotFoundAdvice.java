package Payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice //Give global exception handling advice i.e. this exception is the same for all parts of the app
public class EmployeeNotFoundAdvice {
    @ResponseBody //maps to a JSON that will be able to interpret the following code
    @ExceptionHandler(EmployeeNotFoundException.class) //Specifies to only respond to this exception
    @ResponseStatus(HttpStatus.NOT_FOUND) //Specifies the response to issue for the exception
    public String EmployeeNotFoundHandler(EmployeeNotFoundException ex) {
        //will put into response body the excpetion message
        return ex.getMessage();
    }
}
