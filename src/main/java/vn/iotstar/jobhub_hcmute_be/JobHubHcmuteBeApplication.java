package vn.iotstar.jobhub_hcmute_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.time.Duration;

@SpringBootApplication
public class JobHubHcmuteBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobHubHcmuteBeApplication.class, args);
    }

}
