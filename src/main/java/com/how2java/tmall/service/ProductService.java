package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;

import java.util.List;

/**
 */
public interface ProductService {
    void add(Product p);

    void delete(int id);

    void update(Product p);

    Product get(int id);

    List list(int cid);

    void setFirstProductImage(Product p);

    void fill(Category c);

    void fill(List<Category> cs);

    void fillByRow(List<Category> cs);

    //设置产品销量和产品累计评价
    void setSaleAndReviewNumber(Product p);

    void setSaleAndReviewNumber(List<Product> ps);

    //模糊查询
    List<Product> search(String keyword);
}
