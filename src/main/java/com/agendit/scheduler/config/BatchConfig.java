package com.agendit.scheduler.config;

import com.agendit.scheduler.httpclient.BackendHttpClient;
import com.agendit.scheduler.model.ReminderConfig;
import com.agendit.scheduler.service.EmailService;
import com.agendit.scheduler.tasks.EmailReminder;
import com.agendit.scheduler.tasks.SMSReminder;
import com.agendit.scheduler.tasks.WhatsAppReminder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Step stepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, ReminderConfig reminderConfig, BackendHttpClient backendHttpClient){
        return new StepBuilder("stepOne", jobRepository).tasklet(new WhatsAppReminder(reminderConfig, backendHttpClient), transactionManager).build();
    }

    @Bean
    public Step stepTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager, EmailService emailService, ReminderConfig reminderConfig, BackendHttpClient backendHttpClient){
        return new StepBuilder("stepTwo", jobRepository).tasklet(new EmailReminder(emailService, reminderConfig, backendHttpClient), transactionManager).build();
    }

    @Bean(name="demoJobOne")
    public Job demoJobOne(JobRepository jobRepository, PlatformTransactionManager transactionManager, EmailService emailService, ReminderConfig reminderConfig, BackendHttpClient backendHttpClient){
        return new JobBuilder("demoJobOne", jobRepository)
                .start(stepOne(jobRepository, transactionManager, reminderConfig, backendHttpClient))
                .next(stepTwo(jobRepository, transactionManager, emailService, reminderConfig, backendHttpClient))
                .build();
    }
}