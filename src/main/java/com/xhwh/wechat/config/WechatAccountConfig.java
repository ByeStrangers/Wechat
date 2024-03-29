package com.xhwh.wechat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat")
@Getter
@Setter
public class WechatAccountConfig {
    private String mpAppId; //微信公众号APPID
    private String mpAppSecret; //微信公众号密钥
    private String myToken; //自己在公众号定义的接入Token
    private String domain; //当前域名
    private String noteUrl; //培训笔记链接
    private String templateId1; //培训介绍模板
    private String templateId2; //网页授权模板
    private String templateId3; //用户信息模板
}
