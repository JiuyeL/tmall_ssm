package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.OrderItemMapper;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ProductService productService;

    @Override
    public void add(OrderItem oi) {
        orderItemMapper.insert(oi);
    }

    @Override
    public void delete(int id) {
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderItem oi) {
        orderItemMapper.updateByPrimaryKeySelective(oi);
    }

    @Override
    public OrderItem get(int id) {
        OrderItem oi = orderItemMapper.selectByPrimaryKey(id);
        setProduct(oi);
        return oi;
    }

    @Override
    public List<OrderItem> list() {
        OrderItemExample example = new OrderItemExample();
        example.setOrderByClause("id DESC");
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        return ois;
    }

    @Override
    public void fill(List<Order> os) {
        for (Order o : os) {
            fill(o);
        }
    }

    @Override
    public void fill(Order o) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOidEqualTo(o.getId());
        example.setOrderByClause("id DESC");
        List<OrderItem> ois = orderItemMapper.selectByExample(example);

        setProduct(ois);

        int totalNumber = 0;
        float total = 0;
        for (OrderItem oi : ois) {
            totalNumber += oi.getNumber();
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
        }

        o.setTotal(total);
        o.setTotalNumber(totalNumber);
        o.setOrderItems(ois);
    }

    @Override
    public int getSaleCount(int pid) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andPidEqualTo(pid);
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        int saleCount = 0;
        for (OrderItem orderItem : ois) {
            saleCount += orderItem.getNumber();
        }
        return saleCount;
    }

    //根据用户查询用户的所有订单,购物车
    @Override
    public List<OrderItem> listByUser(User user) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andUidEqualTo(user.getId()).andOidIsNull();
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        setProduct(ois);
        return ois;
    }

    //给OrderItem的对象添加产品信息
    public void setProduct(OrderItem oi) {
        Product product = productService.get(oi.getPid());
        oi.setProduct(product);
    }

    public void setProduct(List<OrderItem> ois) {
        for (OrderItem oi : ois) {
            setProduct(oi);
        }
    }
}
