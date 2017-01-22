package com.zhangyu.SpringBootAndRabbit;

import com.zhangyu.SpringBootAndRabbit.controller.RecvController;
import com.zhangyu.SpringBootAndRabbit.controller.SendController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@RestController
@EnableAutoConfiguration
public class App
{
    public static ApplicationContext context;

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }

    @RequestMapping("/h")
    public String home() {
        return "Hello";
    }

    @RequestMapping("/w")
    public String word() {
        return "World";
    }


    public static void main( String[] args )
    {
        System.out.println( "Hello World ! App!" );
//        SpringApplication.run(App.class, args);
        SpringApplication.run(SendController.class);
//        SpringApplication.run(RecvController.class);
    }

}