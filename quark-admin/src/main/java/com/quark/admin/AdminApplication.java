package com.quark.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by lhr on 17-7-31.
 */
@SpringBootApplication
@EnableCaching//缓存支持
public class AdminApplication {



    public static void main(String[] args) {
        //更改properties配置文件名称,避免依赖冲突

        SpringApplication app = new SpringApplication(AdminApplication.class);

        app.run(args);
//        SpringApplication.run(CommonApplication.class, args);
    }
}
