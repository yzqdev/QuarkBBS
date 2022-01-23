package com.quark.chat;

import com.quark.chat.server.QuarkChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author : ChinaLHR
 * @Date : Create in 22:14 2017/10/19
 * @Email : 13435500980@163.com
 */
@SpringBootApplication
@EnableCaching//缓存支持
public class ChatApplication  implements CommandLineRunner {
    @Resource
    private QuarkChatServer server;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws IOException {

        SpringApplication app = new SpringApplication(ChatApplication.class);
        app.run(args);
    }

    @Bean
    public QuarkChatServer quarkChatServer(){
        return new QuarkChatServer();
    }

    @Override
    public void run(String... strings) {
        server.start();
    }
}
