package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.JobService;

@RestController
@RequestMapping("/api/v1/job")
@Validated
@Tag(name = "Job", description = "Job API")
@EnableCaching
public class JobController {

    final JwtTokenProvider jwtTokenProvider;

    final JobService jobService;
    @Autowired
    ResponseBuild responseBuild;

    public JobController(JwtTokenProvider jwtTokenProvider, JobService jobService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobService = jobService;
    }

    //    @GetMapping("/get-all-jobs")
//
//    public ResponseEntity<GenericResponse> getAllJobs(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size,  @RequestParam(defaultValue = "true") Boolean isActive) {
//        return jobService.getAllJobs(PageRequest.of(index, size), isActive);
//    }
    @GetMapping("/get-all-jobs")
        public ResponseModel getAllJobs(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size,  @RequestParam(defaultValue = "true") Boolean isActive) {
        ActionResult actionResult = null;
        try {
            actionResult = jobService.getAllJob(PageRequest.of(index, size), isActive);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/get-all")
    public ResponseEntity<GenericResponse> getAllJob(@RequestParam(defaultValue = "true") Boolean isActive) {
        return jobService.getAlls(isActive);
    }

    @GetMapping("{employerId}/get-list-jobs")
    public ResponseEntity<?> getJobsByEmployer(@PathVariable("employerId") String id, @RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size) {
        return jobService.findAllByEmployer(id, PageRequest.of(index, size));
    }

    @GetMapping("/detail-job")
    public ResponseEntity<?> getDetail(@RequestParam("jobId") String jobId, @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader != null) {
            String jwt = authorizationHeader.substring(7);
            if (!jwt.isBlank()) {
                String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
                return jobService.getDetail(jobId, userId);
            }
        }
        return jobService.getDetail(jobId);
    }

}
