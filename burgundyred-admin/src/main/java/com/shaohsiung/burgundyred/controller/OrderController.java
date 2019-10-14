package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(version = "1.0.0")
    private OrderService orderService;

    /**
     * 卖家获取订单列表（所有用户）
     * @param pageNum
     * @return
     */
    @GetMapping("/{pageNum}")
    public BaseResponse orderList(@PathVariable("pageNum") Integer pageNum) {
        List<Order> orderList = orderService.orderList(pageNum, AppConstant.ORDER_PAGE_SIZE);

        // 计算总页数
        Integer totalRecord = orderService.orderListTotalRecord();
        Integer totalPage = (totalRecord + AppConstant.ORDER_PAGE_SIZE - 1) / AppConstant.ORDER_PAGE_SIZE;

        Map result = new HashMap();
        result.put("orderList", orderList);
        result.put("page", pageNum);
        result.put("totalPage", totalPage);
        return BaseResponseUtils.success(result);
    }

    /**
     * 卖家发货
     * @return
     */
    @PostMapping("/ship/{orderId}")
    public BaseResponse ship(@PathVariable("orderId") String orderId) {
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
    public BaseResponse confirmCancel(@PathVariable("orderId") String orderId) {
        log.info("【卖家应用】卖家确认取消订单请求，orderId：{}", orderId);
        Order result = orderService.confirmCancel(orderId);
        return BaseResponseUtils.success(result);
    }
}
