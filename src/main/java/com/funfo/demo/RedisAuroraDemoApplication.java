package com.funfo.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * SpringBoot 3.5.8 启动类
 * @MapperScan：扫描 MyBatis Mapper 接口（核心）
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.funfo.demo", "com.aws.demo.service"})
@MapperScan("com.funfo.demo.mapper")  // 必须指定 Mapper 包路径
public class RedisAuroraDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisAuroraDemoApplication.class, args);
        System.out.println("🎉 SpringBoot 3.5.8 服务启动成功！端口：8000");
    }
}
