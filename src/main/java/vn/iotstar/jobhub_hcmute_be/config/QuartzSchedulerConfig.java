package vn.iotstar.jobhub_hcmute_be.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class QuartzSchedulerConfig {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private DailySendNewUser dailyEmailJob;
    @Autowired
    private DailySendNewApplyJob dailySendNewApplyJob;
    @Autowired
    private DailyRecommendForUser dailyRecommendForUser;
    @Autowired
    private DailyRefreshDataCollaborativeFiltering dailyRefreshDataCollaborativeFiltering;

    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleSendNewUser() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DailySendNewUser.class)
                .withIdentity("dailyEmailJob")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dailyEmailTrigger")
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleSendNewApply() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DailySendNewApplyJob.class)
                .withIdentity("dailySendNewApplyJob")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dailySendNewApplyTrigger")
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleRecommendForUser() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DailyRecommendForUser.class)
                .withIdentity("dailyRecommendForUser")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dailyRecommendForUserTrigger")
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
    @Scheduled(cron = "0 15 20 * * ?")
    public void scheduleRefreshDataCollaborativeFiltering() throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(DailyRefreshDataCollaborativeFiltering.class)
                .withIdentity("dailyRefreshDataCollaborativeFiltering")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dailyRefreshDataCollaborativeFilteringTrigger")
                .startNow()
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
