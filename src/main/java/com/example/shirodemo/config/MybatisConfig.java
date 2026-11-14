package com.example.shirodemo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置：指定 mapper 包扫描路径
 */
@Configuration
@MapperScan("com.example.shirodemo.mapper")
public class MybatisConfig {
}
