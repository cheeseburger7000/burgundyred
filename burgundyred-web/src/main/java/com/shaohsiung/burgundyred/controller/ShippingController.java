package com.shaohsiung.burgundyred.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.service.ShippingService;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Reference(version = "1.0.0")
    private ShippingService shippingService;

    @GetMapping("/confirm")
    public String addPageConfirm() {
        return "addShippingConfirm";
    }
    @GetMapping("/info")
    public String addPageInfo() {
        return "addShippingInfo";
    }

    /**
     * 添加物流地址
     *      备注：URL映射为视图返回的位置
     * @return
     */
    @PostMapping("/confirm")
    public String addConfirm(@RequestParam @NotBlank String receiverName,
                      @RequestParam @NotBlank String receiverMobile,
                      @RequestParam @NotBlank String receiverProvince,
                      @RequestParam @NotBlank String receiverCity,
                      @RequestParam @NotBlank String receiverDistrict,
                      @RequestParam @NotBlank String receiverAddress,
                      @RequestParam @NotBlank String receiverZip,
                      HttpServletRequest request) {

        addShipping(receiverName, receiverMobile, receiverProvince, receiverCity, receiverDistrict, receiverAddress, receiverZip, request);

        return "redirect:/order/confirm";
    }

    @PostMapping("/info")
    public String addInfo(@RequestParam @NotBlank String receiverName,
                          @RequestParam @NotBlank String receiverMobile,
                          @RequestParam @NotBlank String receiverProvince,
                          @RequestParam @NotBlank String receiverCity,
                          @RequestParam @NotBlank String receiverDistrict,
                          @RequestParam @NotBlank String receiverAddress,
                          @RequestParam @NotBlank String receiverZip,
                          HttpServletRequest request) {

        addShipping(receiverName, receiverMobile, receiverProvince, receiverCity, receiverDistrict, receiverAddress, receiverZip, request);

        return "redirect:/info";
    }

    @GetMapping("/delete/{shippingId}")
    public String delete(@PathVariable("shippingId") String shippingId, HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        int deleteShipping = shippingService.deleteShipping(shippingId, user.getId());

        return "redirect:/info";
    }

    private void addShipping(@RequestParam @NotBlank String receiverName, @RequestParam @NotBlank String receiverMobile, @RequestParam @NotBlank String receiverProvince, @RequestParam @NotBlank String receiverCity, @RequestParam @NotBlank String receiverDistrict, @RequestParam @NotBlank String receiverAddress, @RequestParam @NotBlank String receiverZip, HttpServletRequest request) {
        // 登陆校验
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new FrontEndException(ErrorState.USER_NOT_LOGGED_IN);
        }

        Shipping shipping = Shipping.builder().userId(user.getId())
                .receiverName(receiverName)
                .receiverMobile(receiverMobile)
                .receiverProvince(receiverProvince)
                .receiverCity(receiverCity)
                .receiverDistrict(receiverDistrict)
                .receiverAddress(receiverAddress)
                .receiverZip(receiverZip)
                .build();

        Shipping added = shippingService.addShipping(shipping);
    }
}
