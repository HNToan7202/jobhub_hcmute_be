package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.JobService;

@RestController
@RequestMapping("/api/v1/job")
@Validated
@Tag(name="Job", description="Job API")
public class JobController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    JobService jobService;

    @GetMapping("/get-all-jobs")
    public ResponseEntity<GenericResponse> getAllJobs(@RequestParam(defaultValue = "0") int index,
                                                      @RequestParam(defaultValue = "10") int size){
        return jobService.getAllJobs(PageRequest.of(index,size));
    }

    @GetMapping("/get-all")
    public ResponseEntity<GenericResponse> getAllJob(){
        return jobService.getAlls();
    }

    @GetMapping("{employerId}/get-list-jobs")
    public ResponseEntity<?> getJobsByEmployer(@PathVariable("employerId") String id,
                                               @RequestParam(defaultValue = "0") int index,
                                               @RequestParam(defaultValue = "10") int size){
        return jobService.findAllByEmployer(id, PageRequest.of(index,size));
    }

    @GetMapping("/get-detail")
    public ResponseEntity<?> getDetail(@RequestParam("jobId") String id){
        return jobService.getDetail(id);
    }

}
