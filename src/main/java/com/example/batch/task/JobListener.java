package com.example.batch.task;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author anonymity
 * @create 2019-08-16 15:16
 **/
@Component
public class JobListener {

    @Resource
    private JobLauncher jobLauncher;
    @Resource
    private Job job;

    /**
     * TODO 开启定时任务时会有一个报错，具体执行代码处，待解决
     * https://github.com/spring-projects/spring-batch/blob/master/spring-batch-infrastructure/src/main/java/org/springframework/batch/item/database/AbstractPagingItemReader.java - line 133
     */
    @Scheduled(fixedRate = 10000)
    public void handle() {
        JobParameters parameters = new JobParametersBuilder().addLong("time-->", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, parameters);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

}
