package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface RecommendationService {
    ActionResult getRecommendationByUserId(String userId);

    ActionResult getRecommendationByJobId(String jobId);
}
