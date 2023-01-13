package Payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {
    private final OrderRepository orderRepo;
    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository or, OrderModelAssembler oma) {
        this.orderRepo = or;
        this.assembler = oma;
    }

    //Mapping GET
    @GetMapping("/orders")
    CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> orders = orderRepo.findAll().stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    //Map get by ID
    @GetMapping("/orders/{id}")
    EntityModel<Order> one(@PathVariable long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    //i.e. the response will contain a model of the order we posted
    //will map the request body of command to the order object
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {
        //set status of new order and save to repo
        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = orderRepo.save(order);

        //return response entity with uri location & order entity model body
        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                .body(assembler.toModel(newOrder));
    }

    @DeleteMapping("/orders/{id}/cancel")
    ResponseEntity<?> cancel(@PathVariable long id) {

        //find order
        Order order = orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        //if order in progress return HTTP 200 OK to confirm cancellation and set status to canceld
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
        }

        //have to return a METHOD_NOT_ALLOWED status if order not in progress
        //returns RFC-7807 PROBLEM for a hypermedia-supporting container i.e. body
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("Cannot cancel order with status:" + order.getStatus()));
    }

    @PutMapping("/orders/{id}/complete")
    ResponseEntity<?> complete(@PathVariable long id) {
        //find our order
        Order order = orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        //if in progress change status and return HTTP 200 OK response
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
        }

        //if not in progress throw RFC-7807 Problem error
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("Cannot complete order with status: " + order.getStatus()));
    }
}
