package com.xhwh.wechat.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    //接受用户消息
    @PostMapping("wechatAccess")
    @ResponseBody
    public void wechatAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WxMpXmlMessage wxMpXmlMessage = WxMpXmlMessage.fromXml(request.getInputStream());
        response.setContentType("text/html;charset=UTF-8"); //解决中文乱码问题
        log.info(wxMpXmlMessage.toString());
        WxMpXmlOutMessage wxMpXmlOutMessage;
        if("image".equals(wxMpXmlMessage.getMsgType())){
            wxMpXmlOutMessage = WxMpXmlOutMessage.IMAGE().toUser(wxMpXmlMessage.getFromUser())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .mediaId(wxMpXmlMessage.getMediaId())
                    .build();
        }else {
            String content = "text".equals(wxMpXmlMessage.getMsgType()) ? wxMpXmlMessage.getContent() : "很抱歉，暂不支持此类型的消息";
            wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .content(content)
                    .build();
        }
        if(wxMpXmlOutMessage != null){
            log.info(wxMpXmlOutMessage.toXml());
            response.getWriter().print(wxMpXmlOutMessage.toXml());
        }else {
            response.getWriter().print("");
        }
    }
}
