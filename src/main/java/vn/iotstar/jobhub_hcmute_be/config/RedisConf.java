package vn.iotstar.jobhub_hcmute_be.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import vn.iotstar.jobhub_hcmute_be.dto.JobDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;
import vn.iotstar.jobhub_hcmute_be.service.JobService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.ArrayList;
import java.util.List;

@EnableCaching
@Configuration
public class RedisConf {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobApplyService jobApplyService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void preloadCache() throws InterruptedException {
        Cache cache = cacheManager.getCache("applicationCache");
        System.out.println("Preloading cache...");

        List<Job> jobList = jobService.findAll();

        List<JobDTO> jobDTOList = new ArrayList<>();

        for(Job job: jobList) {
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            jobDTOList.add(jobDTO);
        }

        for (JobDTO jobDTO : jobDTOList) {
            ActionResult actionResult = new ActionResult();
            actionResult.setData(jobDTO);
            actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_DETAIL_SUCCESS);
            cache.put(jobDTO.getJobId(), actionResult);
        }

    }


    @CacheEvict(value = "applicationCache", allEntries = true)
    public void clearAllCache() {
        System.out.println("Clearing cache...");

    }

    @CacheEvict(value = "applicationCache", key = "#jobId")
    public void clearCache(String jobId) {
        System.out.println("Clearing cache...");
    }

}