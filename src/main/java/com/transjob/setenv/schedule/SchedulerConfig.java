package com.transjob.setenv.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
@Configuration
@EnableScheduling
//@EnableSchedulerLock(defaultLockAtMostFor = "PT60S") 별도의 의존성 설치 필요
public class SchedulerConfig {

    //    @Bean
    //    public LockProvider lockProvider() {
//            return new InMemoryLockProvider();
//        }
}
