package com.cummins.model;

import java.sql.Timestamp;

/**
 * Created by newforesee on 2022/8/23
 */

public class Order {
    Integer order_id;
    Timestamp order_date;
    String customer_name;
    Double price;
    Integer product_id;
    Boolean order_status;


    public Integer getOrder_id() {
        return order_id;
    }

    public Order setOrder_id(Integer order_id) {
        this.order_id = order_id;
        return this;
    }

    public Timestamp getOrder_date() {
        return order_date;
    }

    public Order setOrder_date(Timestamp order_date) {
        this.order_date = order_date;
        return this;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public Order setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public Order setPrice(Double price) {
        this.price = price;
        return this;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public Order setProduct_id(Integer product_id) {
        this.product_id = product_id;
        return this;
    }

    public Boolean getOrder_status() {
        return order_status;
    }

    public Order setOrder_status(Boolean order_status) {
        this.order_status = order_status;
        return this;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id=" + order_id +
                ", order_date=" + order_date +
                ", customer_name='" + customer_name + '\'' +
                ", price=" + price +
                ", product_id=" + product_id +
                ", order_status=" + order_status +
                '}';
    }

    public Order() {
    }

    public Order(Integer order_id, Timestamp order_date, String customer_name, Double price, Integer product_id, Boolean order_status) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.customer_name = customer_name;
        this.price = price;
        this.product_id = product_id;
        this.order_status = order_status;
    }
}
