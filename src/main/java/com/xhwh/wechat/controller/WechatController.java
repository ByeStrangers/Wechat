package com.xhwh.wechat.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class WechatController {
    @Autowired
    private WxMpService wxMpService;

    //微信公众号接入
    @GetMapping("wechatAccess")
    @ResponseBody
    public String wechatAccess(String signature,String timestamp,String nonce,String echostr){
        log.info("signature: {}, timestamp: {}, nonce: {}, echostr: {}", signature, timestamp, nonce, echostr);
        if(wxMpService.checkSignature(timestamp, nonce,signature)){
            return echostr; //接入校验合法，返回
        }
        return null;
    }
}
