package vn.iotstar.jobhub_hcmute_be.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.iotstar.jobhub_hcmute_be.service.Impl.RedisServiceImpl;
import vn.iotstar.jobhub_hcmute_be.utils.Constants;

import java.time.LocalDate;

@Component
public class DailyRefreshDataCollaborativeFiltering extends RedisServiceImpl implements Job {
    @Autowired
    private WebClient webClient;

    public DailyRefreshDataCollaborativeFiltering(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    public Mono<String> refresh_data_r() {
        return webClient.get()
                .uri("/refresh-data")
                .retrieve()
                .bodyToMono(String.class);
    }
    private void handel() {
        try {
            Mono<String> resultMono = refresh_data_r();
            String result = resultMono.block();
            System.out.println(result);
            LocalDate today = LocalDate.now();
            this.hashSet(Constants.REFRESH_DATA_RECOMMEND,today.toString(), result);
        } catch (Exception e) {
            e.printStackTrace();
            this.hashSet(Constants.REFRESH_DATA_RECOMMEND,LocalDate.now().toString(), e.getMessage());
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        handel();
    }
}
