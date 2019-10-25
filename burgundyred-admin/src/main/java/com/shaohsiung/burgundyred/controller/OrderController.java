package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.model.Administrator;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials="true", maxAge = 3600)
public class OrderController {

    @Reference(version = "1.0.0")
    private OrderService orderService;

    /**
     * 卖家获取订单列表（所有用户）
     * @param pageNum
     * @return
     */
    @GetMapping("/{pageNum}")
    public BaseResponse orderList(@PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        List<Order> orderList = orderService.orderList(pageNum, AppConstant.ORDER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = orderService.orderListTotalRecord();

        Map result = new HashMap();
        result.put("orderList", orderList);
        result.put("pageSize", AppConstant.ORDER_PAGE_SIZE);
        result.put("totalRecord", totalRecord);
        return BaseResponseUtils.success(result);
    }

    /**
     * 卖家发货
     * @return
     */
    @PostMapping("/ship/{orderId}")
    public BaseResponse ship(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【卖家应用】卖家发货请求，orderId：{}", orderId);
        Order result = orderService.ship(orderId);
        return BaseResponseUtils.success(result);
    }

    /**
     * 卖家确认取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/confirm/{orderId}")
    public BaseResponse confirmCancel(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        Administrator admin = (Administrator) request.getAttribute("admin");
        if (admin == null) {
            throw  new BackEndException(ErrorState.ADMIN_AUTHENTICATION_FAILED);
        }

        log.info("【卖家应用】卖家确认取消订单请求，orderId：{}", orderId);
        Order result = orderService.confirmCancel(orderId);
        return BaseResponseUtils.success(result);
    }
}
