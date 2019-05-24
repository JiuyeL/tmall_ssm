package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderService {

    String waitPay = "waitPay";
    String waitDelivery = "waitDelivery";
    String waitConfirm = "waitConfirm";
    String waitReview = "waitReview";
    String finish = "finish";
    String delete = "delete";

    void add(Order o);

    float add(Order o, List<OrderItem> ois);

    void delete(int id);

    void update(Order o);

    Order get(int id);

    List<Order> list();

    //我的订单里查询各种状态的订单
    List<Order> list(int uid, String excludedStatus);
}
