package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseModel getRecommendationByUserId(@PathVariable("userId") String userId) {
        ActionResult actionResult = recommendationService.getRecommendationByUserId(userId);
        return responseBuild.build(actionResult);
    }
    @PreAuthorize("hasAnyRole( 'EMPLOYER')")
    @RequestMapping("/job/{jobId}")
    public ResponseModel getRecommendationByJobId(@PathVariable("jobId") String jobId) {
        ActionResult actionResult = recommendationService.getRecommendationByJobId(jobId);
        return responseBuild.build(actionResult);
    }

}

