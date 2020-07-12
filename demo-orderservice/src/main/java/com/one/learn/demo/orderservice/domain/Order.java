package com.one.learn.demo.orderservice.domain;

import java.util.Random;

/**
 * @author one
 * @date 2020/07/12
 */
public class Order {
    private Long id;
    private String title;

    public Order(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Order mock() {
        return new Order(new Random().nextLong(), "My Order");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
