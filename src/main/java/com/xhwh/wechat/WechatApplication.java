package com.xhwh.wechat;

import com.xhwh.wechat.config.WechatAccountConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class WechatApplication {
    @Autowired
    private WechatAccountConfig wechatAccountConfig;

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }

    @GetMapping("index")
    @ResponseBody
    public WechatAccountConfig getConfig(){
        return wechatAccountConfig;
    }
}
