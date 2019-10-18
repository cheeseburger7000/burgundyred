package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AlipayCallback;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Iterator;
import java.util.Map;

/**
 * 订单服务
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(version = "1.0.0")
    private OrderService orderService;

    /**
     * TODO 提取支付宝初始化信息
     */
    static {
        Configs.init("zfbinfo.properties");
    }

    /**
     * 创建订单
     * @param shippingId
     * @return
     */
    @PostMapping("/create")
    public BaseResponse create(HttpServletRequest request,
                               @NotBlank @RequestParam("shippingId") String shippingId) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Order order = orderService.create(user.getId(), shippingId);
        return BaseResponseUtils.success(order);
    }

    /**
     * 支付订单
     * @param orderNo
     * @param request
     * @return
     */
    @PostMapping("/pay")
    public BaseResponse pay(@NotBlank @RequestParam("orderNo") String orderNo,
                            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        // 获取存储二维码的目录路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        BaseResponse result = orderService.pay(orderNo, user.getId(), path);
        return result;
    }

    /**
     * 支付宝回调
     * @param request
     * @return
     */
    @RequestMapping("/alipay_callback")
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
    @RequestMapping("/query_order_pay_state")
    public BaseResponse queryOrderPayState(HttpServletRequest request, @NotBlank @RequestParam("orderNo") String orderNo) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        BaseResponse baseResponse = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if(baseResponse.getState().equals(ResultCode.SUCCESS.getCode())){
            return BaseResponseUtils.success(true);
        }
        return BaseResponseUtils.success(false);
    }

    /**
     * 用户取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/cancel")
    public BaseResponse cancel(HttpServletRequest request, @NotBlank @RequestParam("orderId") String orderId) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Order cancel = orderService.cancel(orderId, user.getId());
        return BaseResponseUtils.success(cancel);
    }
}
