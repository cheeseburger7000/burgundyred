package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.ShippingMapper;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.service.ShippingService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Slf4j
@Service(version = "1.0.0")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 用户添加物流信息
     *
     * @param shipping
     * @return
     */
    @Override
    public Shipping addShipping(Shipping shipping) {
        shipping.setId(idWorker.nextId()+"");
        shipping.setCreateTime(new Date());
        shipping.setUpdateTime(new Date());

        int save = shippingMapper.save(shipping);
        if (save == 1) {
            log.info("【物流模块】用户添加物流信息：{}", shipping);
            return shipping;
        }
        throw new FrontEndException("添加物流信息失败");
    }

    /**
     * 获取用户物流信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<Shipping> shippingList(String userId) {
        List<Shipping> result = shippingMapper.getByUserId(userId);
        log.info("【物流模块】获取用户物流信息列表：{}", result);
        return result;
    }

    /**
     * 修改物流信息
     *
     * @param shipping
     * @return
     */
    @Override
    public Shipping updateShipping(Shipping shipping) {
        Shipping origin = shippingMapper.findByIdAndUserId(shipping.getId(), shipping.getUserId());

        if (shipping.getReceiverName() != null) {
            origin.setReceiverName(shipping.getReceiverName());
        }
        if (shipping.getReceiverMobile() != null) {
            origin.setReceiverMobile(shipping.getReceiverMobile());
        }
        if (shipping.getReceiverProvince() != null) {
            origin.setReceiverProvince(shipping.getReceiverProvince());
        }
        if (shipping.getReceiverCity() != null) {
            origin.setReceiverCity(shipping.getReceiverCity());
        }
        if (shipping.getReceiverDistrict() != null) {
            origin.setReceiverDistrict(shipping.getReceiverDistrict());
        }
        if (shipping.getReceiverAddress() != null) {
            origin.setReceiverAddress(shipping.getReceiverAddress());
        }
        if (shipping.getReceiverZip() != null) {
            origin.setReceiverZip(shipping.getReceiverZip());
        }

        origin.setUpdateTime(new Date());
        int update = shippingMapper.update(origin);
        if (update == 1) {
            log.info("【物流模块】用户修改物流信息：{}", origin);
            return origin;
        }
        throw new FrontEndException("修改物流信息失败");
    }

    /**
     * 用户删除物流信息
     *
     * @param ShippingId
     * @return
     */
    @Override
    public int deleteShipping(String ShippingId, String userId) {
        int delete = shippingMapper.deleteByIdAndUserId(ShippingId, userId);
        if (delete == 1) {
            log.info("【物流模块】删除用户：{} 物流信息：{}", userId, ShippingId);
            return delete;
        }
        throw new FrontEndException("删除物流信息失败");
    }
}
