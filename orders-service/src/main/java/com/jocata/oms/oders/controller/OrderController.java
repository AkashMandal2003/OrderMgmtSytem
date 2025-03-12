package com.jocata.oms.oders.controller;

import com.jocata.oms.datamodel.orders.form.OrderForm;
import com.jocata.oms.oders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderForm> createOrder(@RequestBody OrderForm orderForm) {
        return ResponseEntity.ok(orderService.createOrder(orderForm));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderForm> getOrderDetails(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

}
