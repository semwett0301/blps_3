package com.example.code.api;

import com.example.code.model.dto.web.request.RequestCreateOrder;
import com.example.code.model.dto.web.response.ResponseCreateOrder;
import com.example.code.model.dto.web.response.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.*;
import com.example.code.model.mappers.OrderMapper;
import com.example.code.services.DeliveryService.DeliveryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final DeliveryService deliveryService;

    @Autowired
    public OrderController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<ResponseOrder> getOrders() throws UserNotFoundException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return deliveryService.getOrders(username);
    }

    @PostMapping //
    public ResponseCreateOrder createOrder(@RequestBody RequestCreateOrder requestCreateOrder) throws UserNotFoundException, BookIsNotAvailableException, JsonProcessingException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = deliveryService.createOrder(requestCreateOrder.getDay(), requestCreateOrder.getBooks(), username);
        return OrderMapper.INSTANCE.toResponseCreateOrder(order);
    }

    @PostMapping("/{orderId}/accept")
    public void acceptOrder(@PathVariable int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException, JsonProcessingException {
        deliveryService.acceptOrder(orderId);
    }

    @PostMapping("/{orderId}/reject")
    public void declineOrder(@PathVariable int orderId) throws OrderNotFoundException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeIsNotAvailableException {
        deliveryService.choseCourierForOrder(orderId);
    }

    @GetMapping("/{orderId}")
    public ResponseOrder getOrder(@PathVariable int orderId) throws OrderNotFoundException {
        return deliveryService.getOrder(orderId);
    }

    @PostMapping("/{orderId}/complete") //
    public void completeOrder(@PathVariable Integer orderId) throws OrderNotFoundException, JsonProcessingException {
        deliveryService.completeOrder(orderId);
    }

    @PostMapping("/{orderId}/cancel") //
    public void cancelOrder(@PathVariable Integer orderId) throws OrderNotFoundException, JsonProcessingException {
        deliveryService.cancelOrder(orderId);
    }
}
