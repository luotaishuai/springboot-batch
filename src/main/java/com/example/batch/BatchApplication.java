package com.example.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 示例：CSV文件中数据定时导入到数据库
 *
 * @author anonymity
 * @create 2019-08-15 17:32
 **/
@EnableBatchProcessing // 提供用于构建批处理作业的基本配置
@EnableScheduling
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}
