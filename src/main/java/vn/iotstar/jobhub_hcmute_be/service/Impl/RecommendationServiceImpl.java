package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponUserModel;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.RecommendationService;

import java.util.Arrays;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    @Autowired
    private WebClient webClient;

    @Autowired
    private JobRepository jobRepository;


    @Autowired
    private StudentRepository studentRepository;

    public Mono<String[]> getRecommendationByUserIdWebClient(String userId) {
        return webClient.get()
                .uri("/recommendation/jobs/" + userId)
                .retrieve()
                .bodyToMono(String[].class);
    }
    public Mono<String[]> getRecommendationByJobIdWebClient(String jobId) {
        return webClient.get()
                .uri("/recommendation/users/" + jobId)
                .retrieve()
                .bodyToMono(String[].class);
    }
    @Override
    public ActionResult getRecommendationByUserId(String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getRecommendationByUserIdWebClient(userId).block();
            List<Job> jobs = jobRepository.findByJobIdIn(Arrays.asList(result));
            actionResult.setData(jobs);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }

        catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getRecommendationByJobId(String jobId) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getRecommendationByJobIdWebClient(jobId).block();
            List<Student> students = studentRepository.findByUserIdIn(Arrays.asList(result));
            List<ResponUserModel> responUserModels = students.stream().map(ResponUserModel::transform).toList();
            actionResult.setData(responUserModels);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }
        catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

}
