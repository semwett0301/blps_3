package com.example.mailservice.model.dto.kafka;


import com.example.mailservice.model.modelUtils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDTO {
    private int number;

    private OrderStatus orderStatus;

    private int day;

    private Integer startTime;

    private Integer endTime;
}
