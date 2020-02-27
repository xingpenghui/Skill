package com.laoxing.skill.controller;

import com.alipay.api.AlipayApiException;
import com.laoxing.skill.dto.AliPayDto;
import com.laoxing.skill.pay.AliPayUtil;
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
    @GetMapping("api/pay/alipayquery.do")
    @ResponseBody
    public R query(String oid) throws AlipayApiException {
        return R.ok(AliPayUtil.queryPayStatus(oid));
    }
}
