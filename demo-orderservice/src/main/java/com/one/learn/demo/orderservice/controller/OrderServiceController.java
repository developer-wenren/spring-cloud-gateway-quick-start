package com.one.learn.demo.orderservice.controller;

import com.one.learn.demo.orderservice.domain.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author one
 * @date 2020/07/12
 */
@RestController
@RequestMapping("/order")
public class OrderServiceController {
    @RequestMapping("/get")
    public Order get() {
        return Order.mock();
    }
}
