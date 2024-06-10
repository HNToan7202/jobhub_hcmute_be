package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.LinkCV;
import vn.iotstar.jobhub_hcmute_be.dto.SaveToMongoDTO;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendation")
@Tag(name = "Recommendation", description = "Recommendation API")
public class RecommendationController {

    @Autowired
    ResponseBuild responseBuild;
    @Autowired
    RecommendationService recommendationService;

    @PreAuthorize("hasAnyRole( 'STUDENT')")
    @RequestMapping("/user/{userId}")
    public ResponseModel getRecommendationByUserId(@PathVariable("userId") String userId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = recommendationService.getRecommendationByUserId(userId, page, size);
        return responseBuild.build(actionResult);
    }

    @PreAuthorize("hasAnyRole( 'EMPLOYER')")
    @RequestMapping("/job/{jobId}")
    public ResponseModel getRecommendationByJobId(@PathVariable("jobId") String jobId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = recommendationService.getRecommendationByJobId(jobId, page, size);
        return responseBuild.build(actionResult);
    }

    @PreAuthorize("hasAnyRole( 'STUDENT')")
    @PostMapping("/jobs/cv")
    public ResponseModel getRecommendationJobsCv(@Validated @RequestBody LinkCV linkCV) {
        ActionResult actionResult = recommendationService.getJobRecommendationJobsCv(linkCV);
        return responseBuild.build(actionResult);
    }

    @PreAuthorize("hasAnyRole( 'STUDENT')")
    @PutMapping("/analysis/cv")
    public ResponseModel putCVToMongo(@Validated @RequestBody SaveToMongoDTO save) {
        ActionResult actionResult = recommendationService.getLinkCVAndSaveToMongo(save.getLink_cv(), save.getUser_id());
        return responseBuild.build(actionResult);
    }


    @GetMapping("/job-similar/{jobId}")
    public ResponseModel getRecommendationBJobSimilar(@PathVariable("jobId") String jobId,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = recommendationService.getRecommendationBJobSimilar(jobId, page, size);
        return responseBuild.build(actionResult);
    }

    @GetMapping("/reomendation_candidate")
    public ResponseModel getRecommendationCandidate(@RequestParam("jobId") String jobId,
                                                    @RequestParam(defaultValue = "10") Integer no_of_cv) {
        ActionResult actionResult = recommendationService.getRecommendUserByJobId(jobId, no_of_cv);
        return responseBuild.build(actionResult);
    }

    @GetMapping("recommendation_application_CV")
    public ResponseModel getRecommendationApplicationCV(@RequestParam("jobId") String jobId,
                                                        @RequestParam(defaultValue = "10") Integer no_of_cv) {
        ActionResult actionResult = recommendationService.getRecommendUserByJobApplicant(jobId, no_of_cv);
        return responseBuild.build(actionResult);
    }
}

