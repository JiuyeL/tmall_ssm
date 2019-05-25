package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.how2java.tmall.exception.StockExcption;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.JedisPoolUtil;
import comparator.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 */
@Controller
@RequestMapping("")
public class ForeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    UserService userService;
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    /**
     * 主页
     */
    @RequestMapping("forehome")
    public String home(Model model) {
        //没有缓存，从数据库查询
        System.out.println("没有缓存，从数据库查询");
        List<Category> cs = categoryService.list();
        //首页的每个分类下面有五个产品
        productService.fill(cs);
        //点击首页的左侧分类链接，显示分类产品
        productService.fillByRow(cs);
        model.addAttribute("cs", cs);
        return "fore/home";
    }

/*
@RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user == null)
            return "fail";
        session.setAttribute("user", user);
        return "success";
    }
 */
    @RequestMapping("forecheckName")
    @ResponseBody
    public String checkName(Model model, @RequestParam("name") String name){
        System.out.println("*******************************HEloo");
        name = HtmlUtils.htmlEscape(name);
        boolean exist = userService.isExist(name);
        if (exist) {
            return "fail";
        }
        return "success";
    }
    /**
     * 注册
     */
    @RequestMapping("foreregister")
    public String register(Model model, User user, String checkCode, HttpSession session) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);


        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //保证验证码只能使用一次
        session.removeAttribute("CHECKCODE_SERVER");
        //2 判断验证码是否正确
        if (!checkCode.equalsIgnoreCase(checkcode_server)) {
            String msg = "验证码错误";
            model.addAttribute("msg", msg);
            model.addAttribute("user", null);
            return "fore/register";
        }

        userService.add(user);
        return "redirect:registerSuccessPage";
    }

    /**
     * 登录
     */
    @RequestMapping("forelogin")
    public String login(Model model, @RequestParam("name") String name, @RequestParam("password") String password, HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user == null) {
            model.addAttribute("msg", "账户或密码错误");
            return "fore/login";
        }
        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    /**
     * 退出
     */
    @RequestMapping("forelogout")
    public String logout(Model model, HttpSession session) {
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    /**
     * 产品详情页面
     */
    @RequestMapping("foreproduct")
    public String product(Model model, int pid) {
        Product p = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(pid, ProductImageService.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(pid);
        List<Review> rs = reviewService.list(pid);
        productService.setSaleAndReviewNumber(p);

        model.addAttribute("pvs", pvs);
        model.addAttribute("rs", rs);
        model.addAttribute("p", p);
        return "fore/product";
    }

    /**
     * 判断当前是否为登录状态
     */
    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null)
            return "success";
        else return "fail";
    }

    /**
     * 模态登录
     */
    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user == null)
            return "fail";
        session.setAttribute("user", user);
        return "success";
    }

    /**
     * 产品分类页面，通过点击分类链接进入
     */
    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        if (sort != null) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        model.addAttribute("c", c);
        return "fore/category";
    }

    /**
     * 通过关键字搜索商品
     */
    @RequestMapping("foresearch")
    public String search(String keyword, Model model) {
        PageHelper.offsetPage(0, 20);
        List<Product> ps = productService.search(keyword);
        productService.setSaleAndReviewNumber(ps);
        model.addAttribute("ps", ps);
        return "fore/searchResult";
    }

    /**
     * 点击 立即购买 按钮后，判断购物车是否有该商品来决定是否新建订单项
     * 并且进入 填地址那一栏
     */
    @RequestMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) throws StockExcption {
        Product p = productService.get(pid);
        int oiid = 0;
        if (p.getStock() < num || p.getStock() == 0){
            ModelAndView mv = new ModelAndView();
            // 存入错误的提示信息
            mv.addObject("message", "库存不足");
            // 跳转的Jsp页面
            mv.setViewName("error/error");
            return mv;
        }


        User user = (User) session.getAttribute("user");

        boolean fond = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == p.getId().intValue()) {
                //购物车已经存在该产品,但并没有生成订单
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                oiid = oi.getId();
                fond = true;
                break;
            }
        }
        if (!fond) {
            //创建新的订单
            OrderItem oi = new OrderItem();
            oi.setProduct(p);
            oi.setPid(p.getId());
            oi.setNumber(num);
            oi.setUid(user.getId());
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return "redirect:forebuy?oiid=" + oiid;
    }

    /**
     * 进入  地址栏  后的操作
     * 直接购买 或 在购物车购买，把购买商品放到session域中去
     * 为 支付页面 提供数据
     */
    @RequestMapping("forebuy")
    public String buy(Model model, String[] oiid, HttpSession session) {
        List<OrderItem> ois = new ArrayList<OrderItem>();
        float total = 0;
        for (String strOiid : oiid) {
            int id = Integer.parseInt(strOiid);
            OrderItem oi = orderItemService.get(id);
            ois.add(oi);
            Product product = oi.getProduct();
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
        }

        session.setAttribute("ois", ois);
        model.addAttribute("total", total);
        return "fore/buy";
    }

    /**
     * 添加到购物车
     */
    @RequestMapping("foreaddCart")
    @ResponseBody
    public Object addCart(int pid, int num, HttpSession session) throws StockExcption {
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");

        if (p.getStock() < num || p.getStock() ==0){
            ModelAndView mv = new ModelAndView();
            // 存入错误的提示信息
            mv.addObject("message", "库存不足");
            // 跳转的Jsp页面
            mv.setViewName("error/error");
            return mv;
        }
        boolean fond = true;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getPid().intValue() == p.getId().intValue()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                fond = false;
                break;
            }
        }
        if (fond) {
            OrderItem oi = new OrderItem();
            oi.setNumber(num);
            oi.setPid(pid);
            oi.setProduct(p);
            oi.setUid(user.getId());
            orderItemService.add(oi);
        }
        return "success";
    }

    /**
     * 查看购物车
     */
    @RequestMapping("forecart")
    public String cart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        model.addAttribute("ois", ois);
        return "fore/cart";
    }

    /**
     * 购物车页面操作，添加或减少数量
     */
    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(Model model, HttpSession session, int pid, int number) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "fail";
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if (oi.getPid().intValue() == pid) {
                oi.setNumber(number);
                orderItemService.update(oi);
                break;
            }
        }
        model.addAttribute("ois", ois);
        return "success";
    }

    /**
     * 购物车页面操作，删除商品
     */
    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(HttpSession session, int oiid, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "fail";
        orderItemService.delete(oiid);
        List<OrderItem> ois = orderItemService.listByUser(user);
        return "success";
    }

    /**
     * 创建订单
     * 点击提交订单，然后 创建订单，进入支付页面
     */
    @RequestMapping("forecreateOrder")
    public Object createOrder(HttpSession session, Order order) throws StockExcption {
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        //更改商品库存
        for (OrderItem oi : ois) {
            int number = oi.getNumber();
            int pId = oi.getPid();
            Product p = productService.get(pId);
            int stock = p.getStock();
            if (stock < number || stock == 0){
                ModelAndView mv = new ModelAndView();
                // 存入错误的提示信息
                mv.addObject("message", "库存不足");
                // 跳转的Jsp页面
                mv.setViewName("error/error");
                return mv;
            }
            p.setStock(p.getStock() - number);
            productService.update(p);
        }

        //绑定订单和订单项，并以总价格为返回值
        float total = orderService.add(order, ois);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;
    }

    /**
     * 点击 确认支付 按钮， 支付成功成功后进入此方法
     */
    @RequestMapping("forepayed")
    public String payed(int oid, Model model) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed";
    }

    /**
     * 订单页操作，查询所有订单（未删除的）
     * 此处返回的是用户所有的订单，在前台通过点击不同的订单分类（所有、未付款、待发货），筛选出符合状态的订单
     */
    @RequestMapping("forebought")
    public String bought(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.list(user.getId(), OrderService.delete);
        //订单跟订单项结合
        orderItemService.fill(os);
        model.addAttribute("os", os);
        return "fore/bought";
    }

    /**
     * 点击  确认收货  按钮后进入  确认支付  的页面
     * 提交订单的信息到下一个页面
     */
    @RequestMapping("foreconfirmPay")
    public String confirmPay(Model model, int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        model.addAttribute("o", order);
        return "fore/confirmPay";
    }

    /**
     * 点击  确认支付  按钮，进入收货成功页面。
     * 对订单状态、收货时间 进行修改
     */
    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(Model model, int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return "fore/orderConfirmed";
    }

    /**
     * 用户前台操作，删除订单
     */
    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(Model model, int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return "success";
    }

    /**
     * 点击 评价  按钮，进入商品评价页面
     */
    @RequestMapping("forereview")
    public String review(Model model, int oid) {
        //1.1 获取参数oid
        //1.2 根据oid获取订单对象o
        Order o = orderService.get(oid);
        //1.3 为订单对象填充订单项
        orderItemService.fill(o);
        //1.4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了
        Product p = o.getOrderItems().get(0).getProduct();
        //1.5 获取这个产品的评价集合
        List<Review> reviews = reviewService.list(p.getId());
        //1.6 为产品设置评价数量和销量
        productService.setSaleAndReviewNumber(p);
        //1.7 把产品，订单和评价集合放在request上
        model.addAttribute("p", p);
        model.addAttribute("o", o);
        model.addAttribute("reviews", reviews);
        //1.8 服务端跳转到 review.jsp
        return "fore/review";
    }

    /**
     * 提交评价
     */
    @RequestMapping("foredoreview")
    public String doreview(Model model, int oid, int pid, String content, HttpSession session) {
//        1.2 根据oid获取订单对象o
        Order o = orderService.get(oid);
//        1.3 修改订单对象状态
        o.setStatus(OrderService.finish);
//        1.4 更新订单对象到数据库
        orderService.update(o);
//        1.6 根据pid获取产品对象
        Product p = productService.get(pid);
//        1.8 对评价信息进行转义，道理同注册ForeController.register()
        content = HtmlUtils.htmlEscape(content);
//        1.9 从session中获取当前用户
        User user = (User) session.getAttribute("user");
//        1.10 创建评价对象review
        Review review = new Review();
//        1.11 为评价对象review设置 评价信息，产品，时间，用户
        review.setContent(content);
        review.setCreateDate(new Date());
        review.setPid(p.getId());
        review.setUid(user.getId());
//        1.12 增加到数据库
        reviewService.add(review);
//        1.13.客户端跳转到/forereview： 评价产品页面，并带上参数showonly=true
        return "redirect:forereview?oid=" + oid + "&showonly=true";
    }
}
