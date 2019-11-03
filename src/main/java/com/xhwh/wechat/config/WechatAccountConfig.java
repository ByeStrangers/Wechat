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
}
