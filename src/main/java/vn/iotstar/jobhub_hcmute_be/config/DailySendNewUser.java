package vn.iotstar.jobhub_hcmute_be.config;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iotstar.jobhub_hcmute_be.service.Impl.MailServiceVs2Impl;

@Component
public class DailySendNewUser implements Job {
    @Autowired
    private MailServiceVs2Impl mailService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        mailService.sendNewUser(0, 100);

    }

}
