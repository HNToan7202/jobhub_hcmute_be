package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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


    final
    ResponseBuild responseBuild;

    @GetMapping("/clear-cache")
    public String clearCache() {
        jobService.clearAllCache();
        return "Cache cleared";
    }

    public JobController(JwtTokenProvider jwtTokenProvider, JobService jobService, ResponseBuild responseBuild) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobService = jobService;
        this.responseBuild = responseBuild;
    }

    @GetMapping("/get-all-jobs")
    public ResponseModel getAllJobs(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "true") Boolean isActive) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.getAllJobs(PageRequest.of(index, size), isActive);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }
//    @GetMapping("/get-all-job")
//    public ResponseModel getAllJob(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size) {
//        ActionResult actionResult = new ActionResult();
//        try {
//            actionResult = jobElasticsearchService.findAllJobs(PageRequest.of(index, size));
//        } catch (Exception e) {
//            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
//        }
//        return responseBuild.build(actionResult);
//    }
//
//    @GetMapping("/find-job")
//    public ResponseModel findJob(@RequestParam String name, @RequestParam String location, @RequestParam String position, @RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size){
//        ActionResult actionResult = new ActionResult();
//        try {
//            actionResult = jobElasticsearchService.searchJobs(name, location, position, PageRequest.of(index, size));
//        } catch (Exception e) {
//            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
//        }
//        return responseBuild.build(actionResult);
//    }

    @GetMapping("/get-all")
    public ResponseModel getAllJob(@RequestParam(defaultValue = "true") Boolean isActive) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.getAlls(isActive);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("{employerId}/get-list-jobs")
    public ResponseModel getJobsByEmployer(@PathVariable("employerId") String id, @RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.findAllByEmployer(id, PageRequest.of(index, size));
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/detail-job")
    public ResponseModel getDetail(@RequestParam("jobId") String jobId, @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        ActionResult actionResult;
        if (authorizationHeader != null) {
            String jwt = authorizationHeader.substring(7);
            if (!jwt.isBlank()) {
                String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
                actionResult = jobService.getDetail(jobId, userId);
                return responseBuild.build(actionResult);
            }
        }
        actionResult = jobService.getDetail(jobId);
        return responseBuild.build(actionResult);
    }

    @GetMapping
    public ResponseModel getAllJobsByFilters(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String posName,
                                             @RequestParam(defaultValue = "") String location,
                                             @RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size){
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.getAllJobsByFilters(name,posName, location, PageRequest.of(index,size));
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }



}
