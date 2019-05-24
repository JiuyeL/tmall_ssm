package com.how2java.tmall.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 */
@Controller
@RequestMapping("")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @RequestMapping("admin_order_list")
    public String list(Model model, Page page) {
        //设置分页查询的条件
        PageHelper.offsetPage(page.getStart(), page.getCount());
        //查询所有的订单
        List<Order> os = orderService.list();
        //获取总的订单数并绑定到page对象上去
        int total = (int) new PageInfo<>(os).getTotal();
        page.setTotal(total);
        //绑定订单和订单项
        orderItemService.fill(os);
        //存储数据到域对象
        model.addAttribute("os", os);
        model.addAttribute("page", page);
        return "admin/listOrder";
    }

    //发货
    @RequestMapping("admin_order_delivery")
    public String deliver(Order o) {
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return "redirect:admin_order_list";
    }
}
