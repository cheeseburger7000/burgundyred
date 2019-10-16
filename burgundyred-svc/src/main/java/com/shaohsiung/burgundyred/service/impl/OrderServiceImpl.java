package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Maps;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.api.ResultCode;
import com.shaohsiung.burgundyred.constant.AlipayCallback;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.CartItem;
import com.shaohsiung.burgundyred.dto.OrderDetailDto;
import com.shaohsiung.burgundyred.dto.ProductStockDto;
import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.enums.PayPlatformEnum;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.OrderItemMapper;
import com.shaohsiung.burgundyred.mapper.OrderMapper;
import com.shaohsiung.burgundyred.mapper.PayInfoMapper;
import com.shaohsiung.burgundyred.model.*;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.ShippingService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.BigDecimalUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import com.shaohsiung.burgundyred.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service(version = "1.0.0")
public class OrderServiceImpl implements OrderService {

    private static AlipayTradeService tradeService;

    /**
     * init tradeService.
     */
    static {
        Configs.init("zfbinfo.properties");
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private CartService cartService;

    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public Order create(String userId, String shippingId) {

        // 获取购物车，判断购物车存在商品
        Cart cart = cartService.get(userId);
        if (cart.getContent().size() <= 0) {
            throw new FrontEndException(ErrorState.CART_IS_EMPTY);
        }

        // 创建订单对象
        Order order = Order.builder().id(idWorker.nextId() + "")
                .state(OrderState.UNPAID)
                .userId(userId)
                .shippingId(shippingId)
                .orderNo(idWorker.nextId() + "")
                .total(cart.getTotal())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        orderMapper.save(order);

        List<ProductStockDto> productStockDtoList = new ArrayList();

        // 创建订单项对象
        Map<String, CartItem> cartItemMap = cart.getContent();
        for (Map.Entry<String, CartItem> cartItemEntry : cartItemMap.entrySet()) {
            CartItem cartItem = cartItemEntry.getValue();
            OrderItem orderItem = new OrderItem();

            BeanUtils.copyProperties(cartItem, orderItem);
            orderItem.setId(idWorker.nextId()+"");
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(order.getOrderNo());

            orderItemMapper.save(orderItem);

            ProductStockDto productStockDto = new ProductStockDto(cartItem.getProductId(), cartItem.getQuantity());
            productStockDtoList.add(productStockDto);
        }

        // 扣除库存
        productService.decreaseStock(productStockDtoList);

        // 清空购物车
        cartService.clear(userId);

        return order;
    }

    /**
     * 支付订单
     *
     * @param orderNo
     * @param userId
     * @param path 存储二维码的文件路径
     * @return
     */
    @Override
    public BaseResponse pay(String orderNo, String userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();

        // 获取订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // 商城唯一订单号（唯一索引）
        String outTradeNo = order.getOrderNo();

        // 订单标题
        String subject = new StringBuilder().append("Burgundyred商城扫码支付, 订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getTotal().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // FIXME 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单号：").append(outTradeNo).append(" 购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.listByOrderNo(orderNo);
        for (OrderItem orderItem : orderItemList) {
            String productId = orderItem.getProductId();
            Product currentProduct = productService.getProductById(productId);

            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId(),
                    currentProduct.getName(),
                    BigDecimalUtils.mul(orderItem.getAmount().doubleValue(), new Double(100).doubleValue()).longValue(), // 查看源码可知，价钱的单位是分
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtils.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        // 发送支付请求
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        // 处理支付响应
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("【支付基础SVC】支付宝预下单成功，orderNo：{}", orderNo);

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);

                // TODO 上传二维码到七牛云
                //try {
                //    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                //} catch (IOException e) {
                //    log.error("上传二维码异常", e);
                //}

                log.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtils.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);

                return BaseResponseUtils.success(ResultCode.ALIPAY_PAY_SUCCESS, resultMap);

            case FAILED:
                log.error("支付宝预下单失败，orderNo:{}", orderNo);
                return BaseResponseUtils.success(ResultCode.ALIPAY_PAY_FAILED);

            case UNKNOWN:
                log.error("系统异常，预下单状态未知，orderNo:{}", orderNo);
                return BaseResponseUtils.success(ResultCode.ALIPAY_PAY_STATE_UNKNOWN);

            default:
                log.error("不支持的交易状态，交易返回异常，orderNo:{}", orderNo);
                return BaseResponseUtils.success(ResultCode.ALIPAY_PAY_STATE_ERROR);
        }
    }
    /**
     * 简单打印支付响应
     * @param response
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    /**
     * 支付回调
     *
     * @param params
     * @return
     */
    @Override
    public BaseResponse aliCallback(Map<String, String> params) {
        String orderNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        Order order = orderMapper.getByOrderNo(orderNo);
        if (order == null) {
            return BaseResponseUtils.failure();
        }
        if (!order.getState().equals(OrderState.UNPAID )){
            return BaseResponseUtils.failure();
        }
        if (AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            // 交易成功，设置订单状态
            order.setState(OrderState.NOT_SHIPPED);
            int update = orderMapper.update(order);
            if (update != 1) {
                log.error("订单状态转化错误！");
            }
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setId(idWorker.nextId() + "");
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(PayPlatformEnum.ALIPAY);
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setCreateTime(new Date());
        payInfo.setUpdateTime(new Date());

        payInfoMapper.insert(payInfo);

        return BaseResponseUtils.success();
    }

    /**
     * 查询订单状态
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public BaseResponse queryOrderPayStatus(String userId, String orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            // TODO 打印日志订单不存在 抛出异常。。。
            return BaseResponseUtils.failure();
        }
        if (!order.getState().equals(OrderState.UNPAID ) && !order.getState().equals(OrderState.CLOSED )) {
            return BaseResponseUtils.success();
        }
        return BaseResponseUtils.failure();
    }

    /**
     * 取消订单
     *
     * 判断订单状态机
     * 防止越权访问
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order cancel(String orderId, String userId) {
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.UNPAID)) {
            order.setState(OrderState.CLOSED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：未支付->已关闭，订单号：{}", orderId);
                return order;
            }
        } else if (state.equals(OrderState.NOT_SHIPPED)) {
            // TODO 退款

            // 恢复库存
            List<OrderItem> orderItemList = orderItemMapper.getOrderItemListByOrderId(orderId);
            List<ProductStockDto> productStockDtoList = orderItemList.stream().map(orderItem -> {
                ProductStockDto productStockDto = new ProductStockDto(orderItem.getProductId(), orderItem.getQuantity());
                return productStockDto;
            }).collect(Collectors.toList());
            productService.increaseStock(productStockDtoList);

            order.setState(OrderState.CLOSED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：未发货->已关闭，订单号：{}", orderId);
                return order;
            }
        } else if (state.equals(OrderState.SHIPPED)) {
            order.setState(OrderState.CANCEL);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：已发货->已取消，订单号：{}", orderId);
                return order;
            }
        } else {
            log.warn("【订单服务】用户取消订单，订单状态异常，订单号：{}", orderId);
            throw new FrontEndException(ErrorState.ORDER_CAN_NOT_CLOSE);
        }

        throw new FrontEndException(ErrorState.ORDER_CANCEL_FAILED);
    }

    /**
     * 获取用户订单列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Order> userOrderList(String userId, int pageNum, int pageSize) {
        int offset = pageNum * pageSize;
        List<Order> orderList= orderMapper.userOrderList(userId, new RowBounds(offset, pageSize));
        return orderList;
    }

    /**
     * 获取用户订单列表总数量
     *
     * @param userId
     * @return
     */
    @Override
    public Integer userOrderListTotalRecord(String userId) {
        return orderMapper.userOrderListTotalRecord(userId);
    }

    /**
     * 根据订单id查询订单详情
     *      包括 订单信息 订单项信息 物流信息
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public OrderDetailDto getById(String orderId, String userId) {
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        List<OrderItem> orderItemList = orderItemMapper.getOrderItemListByOrderId(orderId);
        Shipping shipping = shippingService.getById(order.getShippingId());

        OrderDetailDto orderDetailDto = new OrderDetailDto();
        BeanUtils.copyProperties(order, orderDetailDto);
        orderDetailDto.setOrderItemList(orderItemList);
        orderDetailDto.setShipping(shipping);
        return orderDetailDto;
    }

    /**
     * 收货
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order receipt(String orderId, String userId) {
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.SHIPPED)) {
            order.setState(OrderState.COMPLETED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户收货，订单状态机：已发货->已完成，订单号：{}", orderId);
                return order;
            }
        }
        throw new FrontEndException(ErrorState.ORDER_STATE_TRANSFORM_ERROR);
    }

    /**
     * 卖家发货
     *
     * @param orderId
     * @return
     */
    @Override
    public Order ship(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.NOT_SHIPPED)) {
            order.setState(OrderState.SHIPPED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户收货，订单状态机：未发货->已发货，订单号：{}", orderId);
                return order;
            }
        }
        throw new BackEndException(ErrorState.ORDER_STATE_TRANSFORM_ERROR);
    }

    /**
     * 卖家确认取消订单
     *
     * @param orderId
     * @return
     */
    @Override
    public Order confirmCancel(String orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new FrontEndException(ErrorState.ORDER_NOT_EXIST);
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.CANCEL)) {
            order.setState(OrderState.CLOSED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户收货，订单状态机：取消中->已关闭，订单号：{}", orderId);
                return order;
            }
        }
        throw new BackEndException(ErrorState.ORDER_STATE_TRANSFORM_ERROR);
    }

    /**
     * 卖家获取所有订单列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Order> orderList(int pageNum, int pageSize) {
        int offset = pageNum * pageSize;
        List<Order> orderList= orderMapper.orderList(new RowBounds(offset, pageSize));
        return orderList;
    }

    /**
     * 获取订单列表总数量
     *
     * @return
     */
    @Override
    public Integer orderListTotalRecord() {
        return orderMapper.orderListTotalRecord();
    }
}
