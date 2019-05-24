package com.how2java.tmall.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class StockExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                         Object o, Exception ex) {
        ex.printStackTrace();
        StockExcption e = null;
        // 获取到异常对象
        if (ex instanceof StockExcption){
            e = (StockExcption) ex;
        }else {
            e = new StockExcption("库存不够123。。。");
        }
        ModelAndView mv = new ModelAndView();
        // 存入错误的提示信息
        mv.addObject("message", e.getMessage());
        // 跳转的Jsp页面
        mv.setViewName("error/error");
        return mv;

    }
}
