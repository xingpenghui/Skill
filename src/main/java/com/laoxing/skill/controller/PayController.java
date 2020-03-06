package com.laoxing.skill.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.file.IOUtils;
import com.laoxing.skill.dto.AliPayDto;
import com.laoxing.skill.dto.WxPayDto;
import com.laoxing.skill.pay.AliPayUtil;
import com.laoxing.skill.pay.WxchatPayUtil;
import com.laoxing.skill.util.QrcodeUtil;
import com.laoxing.skill.vo.R;
import io.goeasy.GoEasy;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-27 16:34
 */
@Controller
public class PayController {
  private  GoEasy goEasy = new GoEasy( "https://rest-hangzhou.goeasy.io", "BC-36808744fcda4503a8e8367e44297e9f");

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
    //微信支付的接口
    //20200101006
    //创建支付订单
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
    //查询支付状态
    @GetMapping("api/pay/wxpayquery.do")
    @ResponseBody
    public R queryPay(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.queryPayStatus(oid));
    }
    //取消支付订单 (未付款)
    @GetMapping("api/pay/wxpaycancel.do")
    @ResponseBody
    public R cancel(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.cancelPay(oid));
    }
    //接收微信支付的异步通知 如果说微信支付成功，微信方 会请求我们的接口
    @GetMapping("api/pay/wxchatnotify.do")
    public void notify(HttpServletRequest request,HttpServletResponse response) throws Exception {
       ArrayList list;
       HashMap map1;
        SqlSessionFactoryBean bean;
       //finalize();
       // SoftReference;
       ConcurrentHashMap map2;
        //1.接收消息
        String xml=new String(IOUtils.toByteArray(request.getInputStream()));
        HashMap<String,Object> map=WxchatPayUtil.parseXml(xml);
        //2.更改数据库对应得到订单的支付状态
        if(map.get("result_code").equals("SUCCESS")){
            String oid=map.get("out_trade_no").toString();
            int money=Integer.parseInt(map.get("total_fee").toString());
            //根据查询订单 校验金额是否一致
            //一致 更改订单状态--->未支付--->已支付,代发货
        }

        //省略……

        //3.通知前端 支付结果
        goEasy.publish("channel_orderpay",map.get("result_code").toString());
                //4.返回消息 固定
        response.getWriter().write("<xml>"+
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>");
    }
}