package com.xhwh.wechat.controller;

import com.xhwh.wechat.config.WechatAccountConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Controller
@Slf4j
public class WechatController {
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private WechatAccountConfig wechatAccountConfig;

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
    public void wechatAccess(HttpServletRequest request, HttpServletResponse response) throws IOException, WxErrorException {
        WxMpXmlMessage wxMpXmlMessage = WxMpXmlMessage.fromXml(request.getInputStream());
        response.setContentType("text/html;charset=UTF-8"); //解决中文乱码问题
        log.info(wxMpXmlMessage.toString());
        WxMpXmlOutMessage wxMpXmlOutMessage = null;
        if("image".equals(wxMpXmlMessage.getMsgType())){
            wxMpXmlOutMessage = WxMpXmlOutMessage.IMAGE().toUser(wxMpXmlMessage.getFromUser())
                    .fromUser(wxMpXmlMessage.getToUser())
                    .mediaId(wxMpXmlMessage.getMediaId())
                    .build();
        } else if("event".equals(wxMpXmlMessage.getMsgType())){ //关注/取消关注事件
            String event = wxMpXmlMessage.getEvent(); //subscribe(订阅)、unsubscribe(取消订阅)
            if("subscribe".equals(event)){
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content("[坏笑]欢迎关注，功能展示回复序号：\n" +
                                 "0、展示功能列表\n" +
                                 "1、模板消息\n" +
                                 "2、获取网页授权")
                        .build();
            }else if("unsubscribe".equals(event)){
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content("[奸笑]欢迎再次关注！")
                        .build();
            } else if("TEMPLATESENDJOBFINISH".equals(event)){
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content("[捂脸]模板事件推送！")
                        .build();
            }else {
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content("[捂脸]你这个事件我得去查查怎么搞！")
                        .build();
            }
        } else {
            String content = "text".equals(wxMpXmlMessage.getMsgType()) ? wxMpXmlMessage.getContent() : "很抱歉，暂不支持此类型的消息";
            if("0".equals(content)){
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content("[坏笑]欢迎关注，功能展示回复序号：\n" +
                                "0、展示功能列表\n" +
                                "1、模板消息\n" +
                                "2、获取网页授权")
                        .build();
            } else if("1".equals(content)){
                WxMpTemplateMsgService wxMpTemplateMsgService = wxMpService.getTemplateMsgService();
                WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                        .templateId(wechatAccountConfig.getTemplateId1())
                        .toUser(wxMpXmlMessage.getFromUser())
                        .url(wechatAccountConfig.getNoteUrl())
                        .build();
                wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("title", "小白仔细听，大神请指正","#D890FA"));
                wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword1", "\n" +
                                                                                     "                 1、微信接入\n" +
                                                                                     "                 2、回复消息\n" +
                                                                                     "                 3、模板消息\n" +
                                                                                     "                 4、网页授权","#768BF7"));
                wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword2", "11","#FA866F"));
                wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword3", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"),"#81EAD8"));
                wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("remark", "\n本次培训仅仅是引领未开发过微信公众号的小哥哥有个大概的了解，讲的不好的地方请大家谅解，本次培训文档参考详情，谢谢大家！","#F5C495"));
                wxMpTemplateMsgService.sendTemplateMsg(wxMpTemplateMessage);
            } else if("2".equals(content)){
                buildAuthUrl(wxMpXmlMessage.getFromUser());
            }else {
                wxMpXmlOutMessage = WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser())
                        .fromUser(wxMpXmlMessage.getToUser())
                        .content(content)
                        .build();
            }
        }
        if(wxMpXmlOutMessage != null){
            log.info(wxMpXmlOutMessage.toXml());
            response.getWriter().print(wxMpXmlOutMessage.toXml());
        }else {
            response.getWriter().print("");
        }
    }

    //拼接网页授权链接
    private void buildAuthUrl(String openId) throws WxErrorException {
        String url = wechatAccountConfig.getDomain() + "/getUserInfo";
        WxMpTemplateMsgService wxMpTemplateMsgService = wxMpService.getTemplateMsgService();
        String authUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, "");
        log.info("authUrl = {}", authUrl);
        WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                .templateId(wechatAccountConfig.getTemplateId2())
                .toUser(openId)
                .url(authUrl)
                .build();
        wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("title", "请点击详情进行网页授权","#D890FA"));
        wxMpTemplateMsgService.sendTemplateMsg(wxMpTemplateMessage);
    }

    @GetMapping("getUserInfo")
    @ResponseBody
    public void getUserInfo(@RequestParam("code") String code, @RequestParam("state") String state){
        log.info("code = {}, state = {}", code, state);
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            log.info(wxMpOAuth2AccessToken.toString());
            if(wxMpOAuth2AccessToken != null && wxMpService.oauth2validateAccessToken(wxMpOAuth2AccessToken)){
                WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
                if(wxMpUser != null){
                    log.info(wxMpUser.toString());
                    WxMpTemplateMsgService wxMpTemplateMsgService = wxMpService.getTemplateMsgService();
                    WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                            .templateId(wechatAccountConfig.getTemplateId3())
                            .toUser(wxMpUser.getOpenId())
                            .url(wxMpUser.getHeadImgUrl())
                            .build();
                    wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("title", "您的个人信息","#D890FA"));
                    wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword1", wxMpUser.getNickname(),"#768BF7"));
                    wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword2", wxMpUser.getSex(), "#FA866F"));
                    wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("keyword3", String.format("%s-%s省-%s市", wxMpUser.getCountry(), wxMpUser.getProvince(), wxMpUser.getCity()),"#81EAD8"));
                    wxMpTemplateMessage.addWxMpTemplateData(new WxMpTemplateData("remark", "\n头像信息请点击详情阅览","#F5C495"));
                    wxMpTemplateMsgService.sendTemplateMsg(wxMpTemplateMessage);
                }
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }
}
