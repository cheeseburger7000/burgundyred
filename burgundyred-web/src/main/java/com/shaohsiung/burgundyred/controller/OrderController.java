package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AlipayCallback;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.OrderDetailDto;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.service.ShippingService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 订单服务
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/order")
public class OrderController {

    @Reference(version = "1.0.0")
    private OrderService orderService;

    @Reference(version = "1.0.0")
    private CartService cartService;

    @Reference(version = "1.0.0")
    private ShippingService shippingService;

    /**
     * TODO 提取支付宝初始化信息
     */
    static {
        Configs.init("zfbinfo.properties");
    }

    /**
     * 用户确认订单
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/confirm")
    public String confirmOrder(HttpServletRequest request,
                               Model model) {

        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Cart cart = cartService.get(user.getId());

        // 根据用户id获取物流信息
        List<Shipping> shippings = shippingService.shippingList(user.getId());

        model.addAttribute("cart", cart);
        model.addAttribute("shippings", shippings);

        return "confirm";
    }

    /**
     * 创建订单
     * @param shippingId
     * @return
     */
    @GetMapping("/create/{shippingId}")
    public String create(HttpServletRequest request,
                               Model model,
                               @PathVariable("shippingId") String shippingId) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Order order = orderService.create(user.getId(), shippingId);

        return "redirect:/order/pay/" + order.getOrderNo();
    }

    /**
     * 支付订单
     * @param orderNo
     * @param request
     * @return
     */
    @GetMapping("/pay/{orderNo}")
    public String pay(@PathVariable("orderNo") String orderNo,
                            HttpServletRequest request,
                      Model model) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        // 获取存储二维码的目录路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        BaseResponse result = orderService.pay(orderNo, user.getId(), path);

        log.info("result: {}", result);

        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        Map<String, String> resultMap = (Map<String, String>) result.getData();
        if (resultMap != null) {
            model.addAttribute("qrcode", resultMap.get("qrUrl"));
        }

        model.addAttribute("orderNo", orderNo);
        model.addAttribute("total", order.getTotal());

        return "pay";
    }

    /**
     * 支付宝回调
     * @param request
     * @return
     */
    @RequestMapping("/alipay_callback")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){

                valueStr = (i == values.length -1)? valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name, valueStr);
        }
        log.info("【支付宝回调】sign:{}, trade_status:{}, params:{}", params.get("sign"), params.get("trade_status"), params.toString());

        // 非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
            if(!alipayRSACheckedV2){
                return BaseResponseUtils.failure();
            }
        } catch (AlipayApiException ex) {
            log.error("【支付宝回调】验证回调异常", ex);
        }

        //TODO 验证各种数据

        BaseResponse baseResponse = orderService.aliCallback(params);
        if(baseResponse.getState().equals(ResultCode.SUCCESS.getCode())){
            return AlipayCallback.RESPONSE_SUCCESS;
        }
        return AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param orderNo
     * @return
     */
    @RequestMapping("/query_order_pay_state/{orderNo}")
    @ResponseBody
    public BaseResponse queryOrderPayState(HttpServletRequest request, @PathVariable("orderNo") String orderNo) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        BaseResponse baseResponse = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if(baseResponse.getState().equals(ResultCode.SUCCESS.getCode())){
            return BaseResponseUtils.success(true);
        }
        return BaseResponseUtils.failure(false);
    }

    @GetMapping("/success/{orderNo}")
    public String paySuccess(@PathVariable("orderNo") String orderNo,
                             Model model) {
        model.addAttribute("message", "订单号：" + orderNo + "，支付成功！");
        return "message";
    }

    /**
     * 用户取消订单
     * @param orderId
     * @return
     */
    @GetMapping("/cancel/{orderId}")
    public String cancel(HttpServletRequest request, @PathVariable("orderId") String orderId) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Order cancel = orderService.cancel(orderId, user.getId());

        return "redirect:/order/orders/0";
//        return BaseResponseUtils.success(cancel);
    }

    @GetMapping("/orders/{pageNum}")
    public String userOrders(HttpServletRequest request, Model model, @PathVariable("pageNum") Integer pageNum) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        List<Order> orderList = orderService.userOrderList(user.getId(), pageNum, AppConstant.ORDER_PAGE_SIZE);
        Integer totalRecord = orderService.orderListTotalRecordByUserId(user.getId());
        Integer totalPage = (totalRecord + AppConstant.ORDER_PAGE_SIZE -1) / AppConstant.ORDER_PAGE_SIZE;

        model.addAttribute("orderList", orderList);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", AppConstant.ORDER_PAGE_SIZE);
        model.addAttribute("totalRecord", totalRecord);
        model.addAttribute("totalPage", totalPage);

        return "orders";
    }

    @GetMapping("/receipt/{orderId}")
    public String receipt(@PathVariable("orderId") String orderId, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Order receipt = orderService.receipt(orderId, user.getId());

        return "redirect:/order/orders/0";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") String orderId,
                              HttpServletRequest request,
                              Model model) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        OrderDetailDto orderDetailDto = orderService.getById(orderId, user.getId());

        model.addAttribute("orderDetailDto", orderDetailDto);
        return "orderDetail";
    }
}
