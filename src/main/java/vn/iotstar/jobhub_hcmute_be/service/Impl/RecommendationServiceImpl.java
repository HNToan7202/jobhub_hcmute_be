package vn.iotstar.jobhub_hcmute_be.service.Impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.iotstar.jobhub_hcmute_be.dto.LinkCV;
import vn.iotstar.jobhub_hcmute_be.dto.PutResumeApplyDTO;
import vn.iotstar.jobhub_hcmute_be.dto.RecommendJobWithCVDTO;
import vn.iotstar.jobhub_hcmute_be.dto.SaveToMongoDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.model.ResponUserModel;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.RecommendationService;
import com.fasterxml.jackson.core.type.TypeReference;

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


    public Mono<String[]> getRecommendationBJobSimilar(String jobId) {
        return webClient.get()
                .uri("/recommendation/job_detail/" + jobId)
                .retrieve()
                .bodyToMono(String[].class);
    }

    public Mono<String[]> putCVToMongo(String linkCV, String userId) {
        return webClient.put()
                .uri("/save-to-mongo")
                .bodyValue(new SaveToMongoDTO(linkCV, userId))
                .retrieve()
                .bodyToMono(String[].class);
    }

    public Mono<String[]> putCVApplyToMongo(PutResumeApplyDTO dto) {
        return webClient.put()
                .uri("/save_to_mong_application_CV")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String[].class);
    }

    public Mono<String[]> getReomendationCandidate(String jobId, int noOfCv) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reomendation_candidate")
                        .queryParam("job_id", jobId)
                        .queryParam("no_of_cv", noOfCv)
                        .build())
                .retrieve()
                .bodyToMono(String[].class);
    }


    public Mono<RecommendJobWithCVDTO[]> recommendationJobsCv(LinkCV linkCV) {
        return webClient.post()
                .uri("/recommendation-cv")
                .bodyValue(linkCV)
                .retrieve()
                .bodyToMono(RecommendJobWithCVDTO[].class);

    }

    public Mono<List<RecommendJobWithCVDTO>> ListJobsCv(LinkCV linkCV) {
        return recommendationJobsCv(linkCV)
                .map(Arrays::asList);
    }

    public Mono<List<RecommendJobWithCVDTO>> getRecommendations(LinkCV linkCV) {
        return webClient.post()
                .uri("/recommendation-cv")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(linkCV))
                .retrieve()
                .bodyToMono(List.class)
                .map(response -> {
                    // Convert JSON list to a list of RecommendJobWithCVDTO objects
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readValue((JsonParser) response, new TypeReference<List<RecommendJobWithCVDTO>>() {
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public ActionResult getJobRecommendationJobsCv(LinkCV linkCV) {
        ActionResult actionResult = new ActionResult();
        try {
            Mono<List<RecommendJobWithCVDTO>> recommendations = getRecommendations(linkCV);
            List<RecommendJobWithCVDTO> recommendJobWithCVDTOList = recommendations.block();
            actionResult.setData(recommendJobWithCVDTOList);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
            return actionResult;
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;
        }
    }

    @Override
    public ActionResult getRecommendationByUserId(String userId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getRecommendationByUserIdWebClient(userId).block();
            Page<Job> jobs = jobRepository.findByJobIdIn(Arrays.asList(result), PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(jobs));
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public Page<Job> getRecommendationJob(String userId, Integer page, Integer size) {
        try {
            String[] result = getRecommendationByUserIdWebClient(userId).block();
            Page<Job> jobs = jobRepository.findByJobIdIn(Arrays.asList(result), PageRequest.of(page - 1, size));
            return jobs;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ActionResult getRecommendationByJobId(String jobId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getRecommendationByJobIdWebClient(jobId).block();
            Page<Student> students = studentRepository.findByUserIdIn(Arrays.asList(result), PageRequest.of(page - 1, size));
            List<ResponUserModel> responUserModels = students.getContent().stream().map(ResponUserModel::transform).toList();
            actionResult.setData(PageModel.transform(students, responUserModels));
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }


    @Override
    public ActionResult getRecommendationBJobSimilar(String jobId, Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getRecommendationBJobSimilar(jobId).block();
            Page<Job> jobs = jobRepository.findByJobIdIn(Arrays.asList(result), PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(jobs));
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getRecommendUserByJobId(String jobId, int noOfCv) {
        ActionResult actionResult = new ActionResult();
        try {
            String[] result = getReomendationCandidate(jobId, noOfCv).block();
            Page<Student> students = studentRepository.findByUserIdIn(Arrays.asList(result), PageRequest.of(0, noOfCv));
            List<ResponUserModel> responUserModels = students.getContent().stream().map(ResponUserModel::transform).toList();
            actionResult.setData(PageModel.transform(students, responUserModels));
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getLinkCVAndSaveToMongo(String linkCV, String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            //     String result = putCVToMongo(linkCV, userId).block();
            //  actionResult.setData(result);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
}
