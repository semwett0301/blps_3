package com.example.code.services.DeliveryService;

import com.example.code.model.dto.web.response.ResponseOrder;
import com.example.code.model.entities.Order;
import com.example.code.model.exceptions.*;
import com.example.code.model.modelUtils.ReservedBook;
import com.example.code.model.modelUtils.TimePeriod;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DeliveryService {

    List<ResponseOrder> getOrders(String username) throws UserNotFoundException;
    Order createOrder(int day, List<ReservedBook> books, String username) throws UserNotFoundException, BookIsNotAvailableException, JsonProcessingException;
    List<TimePeriod> findAvailableTimePeriods(int orderId) throws OrderNotFoundException, IncorrectTimePeriodException;
    void cancelOrder(int orderId) throws OrderNotFoundException, JsonProcessingException;

    void setTimeForOrder(int orderId, TimePeriod timePeriod) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException, TimeHasBeenAlreadyChosenException;

    void choseCourierForOrder(int orderId) throws OrderNotFoundException, TimeIsNotAvailableException, IncorrectTimePeriodException, OrderHasBeenAlreadyAcceptedException;

    ResponseOrder getOrder(int orderId) throws OrderNotFoundException;

    void acceptOrder(int orderId) throws OrderNotFoundException, OrderHasBeenAlreadyAcceptedException, JsonProcessingException;

    void completeOrder(int orderId) throws OrderNotFoundException, JsonProcessingException;
}
