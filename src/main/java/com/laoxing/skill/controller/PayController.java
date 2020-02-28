package com.laoxing.skill.controller;

import com.alipay.api.AlipayApiException;
import com.laoxing.skill.dto.AliPayDto;
import com.laoxing.skill.dto.WxPayDto;
import com.laoxing.skill.pay.AliPayUtil;
import com.laoxing.skill.pay.WxchatPayUtil;
import com.laoxing.skill.util.QrcodeUtil;
import com.laoxing.skill.vo.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 16:34
 */
@Controller
public class PayController {
    //付款
    @PostMapping("api/pay/alipaypre.do")
    public void pay(AliPayDto dto, HttpServletResponse response) throws AlipayApiException, IOException {
       dto.setTotal_amount(0.01);
        //调用支付方法 获取预支付链接
        String u=AliPayUtil.createPrePay(dto);
       if(u!=null){
           //生成缓存 二维码图片
           BufferedImage bufferedImage=QrcodeUtil.createQrcode(u,400);
           //将图片写出去
           ImageIO.write(bufferedImage,"jpeg",response.getOutputStream());
       }
    }
    //查询支付
    @GetMapping("api/pay/alipayquery.do")
    @ResponseBody
    public R query(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.queryPayStatus(oid));
    }
    //取消支付
    @GetMapping("api/pay/alipaycancel.do")
    @ResponseBody
    public R cancenl(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.cancelPay(oid));
    }
    //退款
    @GetMapping("api/pay/alipayrefund.do")
    @ResponseBody
    public R refund(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.refundPay(oid));
    }
    @GetMapping("api/pay/alipayrefundquery.do")
    @ResponseBody
    public R refundquery(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.queryRefundPay(oid));
    }
    //20200101006
    @PostMapping("api/pay/wxpayprepay.do")
    public void createWxPay(WxPayDto payDto,HttpServletResponse response) throws IOException {
        payDto.setTotal_fee(1);
        String u= WxchatPayUtil.createPay(payDto);
        if(u!=null){
            //生成缓存 二维码图片
            BufferedImage bufferedImage=QrcodeUtil.createQrcode(u,400);
            //将图片写出去
            ImageIO.write(bufferedImage,"jpeg",response.getOutputStream());
        }
    }
}
