package Payroll;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.EntityModel;

import javax.swing.text.html.parser.Entity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {
    private final EmployeeRepository repository;
    //to bring in the functionality of the entity model assembler we have to inject it into the class
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Mapping GET functionality for employees
    // tag::get-aggregate-root[]
    //to make this restful we get all items in repository with their links as well as a top level link to repository
    @GetMapping("/Employee")
    CollectionModel<EntityModel<Employee>> all() {

        //create a list of entity models of employees using stream and then mapping each employee to an entity model
        //and returning all values as a list
        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(assembler::toModel)//essentially means to apply assembler.toModel to all stream items
                .toList();

        //return our list of entity models as JSON collection model and adding self link for entire collection
        //for all collections of models
        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]

    //Mapping POST funcionality for employees
    @PostMapping("/Employee")
    //Request new employee creation and uses as parameter for call
    //Response entity creates an HTTP 201 CREATED message with a LOCATION header in the response
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
        //Uses the .save() to save the new employee and then assembles its entity model
        EntityModel<Employee> employee = assembler.toModel(repository.save(newEmployee));

        //return response entity with .created() to generate location link i.e. self link URI with body of entity model
        return ResponseEntity
                .created(employee.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(employee);
    }

    @GetMapping("/Employee/{id}") //{} specifies that @PathVariable annotation used here
    EntityModel<Employee> one(@PathVariable long id) {
        //-> a lambda and here it is used to throw an exception without a try catch block
        //to make this RESTful we use entity model to prevent having to hard code URI's
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        //Using assembler to build model and return it
        return assembler.toModel(employee);
    }

    //This will create a response entity HTTP 201 CREATED which is better than standard HTTP 200 OK
    @PutMapping("/Employee/{id}") //maps a function to put an employee id
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable long id) {
        //create new employee temp var by mapping newEmployee parts to it and then saving it in repository
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })//if we cant find employee to replace by id then
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });

        //create entity model of updated employee
        EntityModel<Employee> entMod = assembler.toModel(updatedEmployee);

        //generate response entity
        return ResponseEntity
                .created(entMod.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entMod);
    }

    @DeleteMapping("/Employee/{id}") //maps delete functionality
    ResponseEntity<?> deleteEmployee(@PathVariable long id) {
        repository.deleteById(id);

        //returns empty response entity
        return ResponseEntity.noContent().build();
    }
}
