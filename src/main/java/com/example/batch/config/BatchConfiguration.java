package com.example.batch.config;

import com.example.batch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.List;

/**
 * @author anonymity
 * @create 2019-08-15 17:55
 **/
@Configuration
public class BatchConfiguration {

    @Resource
    private JobBuilderFactory jobBuilderFactory;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private EntityManagerFactory entityManagerFactory;

    /**
     * 这个Job名称是importJob
     * 使用了step1
     *
     * @return
     */
    @Bean
    public Job importJob() throws Exception {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
//                .flow(step1())
//                .next(step2())
//                .end()
                .start(step1())
                .next(step2())
                .build();
    }

    /**
     * importUserJob 下的 step1 使用的是：
     * chunk 块处理数据（3条数据处理一次）
     * 指定了：读、处理、写的方法
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(3)
                .reader(reader())
                .processor(processor())
                .writer(writer())
//                                                                      // 详细写法
//                .faultTolerant()
//                    .retry(Exception.class)                           // 重试
//                    .noRetry(ParseException.class)
//                    .retryLimit(1)                                    // 每条记录重试次数
//                    .listener(listener)
//                    .skip(Exception.class)
//                    .skipLimit(500)                                   // 一共允许跳过多少条异常
//                    .taskExecutor(new CustomerAsyncTaskExecutor())    // 设置并发方式执行
//                    .throttleLimit(10)                                // 并发任务书10，默认4
//                    .transactionManager(transactionManager)           // PlatformTransactionManager
                .build();
    }

    @Bean
    public Step step2() throws Exception {
        return stepBuilderFactory.get("step2")
                .<User, User>chunk(1000)
                .reader(reader2())
                .writer(writer2())
                .build();
    }

    @Bean
    public JpaPagingItemReader<User> reader2() throws Exception {
        JpaNativeQueryProvider<User> jpaNativeQueryProvider = new JpaNativeQueryProvider<>();
        jpaNativeQueryProvider.setEntityClass(User.class);
        jpaNativeQueryProvider.setSqlQuery("select * from user where age >= :age");
        jpaNativeQueryProvider.afterPropertiesSet();

        JpaPagingItemReader<User> jpaReader = new JpaPagingItemReader<>();
        jpaReader.setParameterValues(Collections.singletonMap("age", 2));
        jpaReader.setEntityManagerFactory(entityManagerFactory);
        jpaReader.setPageSize(5);
        jpaReader.setQueryProvider(jpaNativeQueryProvider);
        jpaReader.afterPropertiesSet();
        jpaReader.setSaveState(true);
        return jpaReader;
    }

    @Bean
    public ItemWriter<User> writer2() {
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> list) throws Exception {
                list.forEach(System.out::println);
            }
        };
    }

    @Bean
    public FlatFileItemReader<User> reader() {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("user.csv"));
        reader.setLineMapper(lineMapper());
        return reader;
    }

    @Bean
    public LineMapper<User> lineMapper() {
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        String[] names = new String[]{"name", "age"};
        lineTokenizer.setNames(names);

        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        return defaultLineMapper;
    }

    @Bean
    public ItemProcessor<User, User> processor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                return user;
            }
        };
    }

    @Bean
    public JpaItemWriter<User> writer() {
        JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JobExecutionListenerSupport listener() {
        return new JobExecutionListenerSupport() {
            long start;
            long end;

            @Override
            public void beforeJob(JobExecution jobExecution) {
                start = System.currentTimeMillis();
                System.out.println("任务处理开始。。。");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                end = System.currentTimeMillis();
                System.out.println("任务处理结束。。。");
                System.out.println("耗时：" + (end - start) + "ms");
            }
        };
    }
}
