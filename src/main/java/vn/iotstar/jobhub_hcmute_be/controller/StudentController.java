package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.ApplyJobRequest;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.ResumeDTO;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;
import vn.iotstar.jobhub_hcmute_be.service.ResumeService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/student")
@Tag(name="Student", description="Student API")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    ResumeService resumeService;

    @Autowired
    UserService userService;

    @Autowired
    JobApplyService jobApplyService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Procedure("application/json")
    @ResponseBody
    @PostMapping("{studentId}/update-resume")
    public ResponseEntity<?> updateResume(@RequestBody ResumeDTO resumeDTO,
                                          @RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("studentId") String studentId, BindingResult bindingResult) throws Exception {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        if(!userId.equals(studentId)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Unauthorized: Empty or invalid token")
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .build()
                    );
        }
        // Kiểm tra xem các trường bắt buộc có được điền hay không
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult
                            .getFieldError())
                    .getDefaultMessage()
            );
        }
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.updateResume(resumeDTO,user.getUserId());
    }

    @PostMapping("/{jobId}/apply-job")
    public ResponseEntity<?> applyForJob(
            @PathVariable("jobId") String jobId,
            @RequestBody ApplyJobRequest request) {
       UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Xử lý ứng tuyển vào công việc
        return jobApplyService.applyForJob(userDetail.getUserId(), jobId, request.getResumeLink());
    }

    @PutMapping("/post-cv")
    public ResponseEntity<?> uploadResume(@ModelAttribute MultipartFile resumeFile) throws IOException {

        // Kiểm tra kích thước tệp
        if (resumeFile.getSize() > 5 * 1024 * 1024) { // 5MB
            return ResponseEntity.badRequest()
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("File size exceeds the maximum limit of 5MB.")
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Unauthorized: Empty or invalid token")
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .build()
                    );
        }
        UserDetail user = (UserDetail) authentication.getPrincipal();
        String originalFileName = resumeFile.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        // Kiểm tra xem tệp có đúng định dạng (chỉ cho phép .doc, .docx hoặc .pdf)
        if (!fileExtension.equals("doc") && !fileExtension.equals("docx") && !fileExtension.equals("pdf")) {
            return ResponseEntity.badRequest()
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Invalid file format. Only .doc, .docx, and .pdf are allowed.")
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
        }
        // Nếu tệp hợp lệ, gọi dịch vụ để xử lý tệp CV
        return resumeService.uploadResumeFile(resumeFile, user.getUserId());
    }
    @DeleteMapping("/cv/{resumeUploadID}")
    public ResponseEntity<?> deleteCV(@RequestHeader("Authorization") String token, @PathVariable String resumeUploadID) throws IOException {
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return resumeService.deleteResume(resumeUploadID, userId);
    }

    @GetMapping("/detail-resume")
    public ResponseEntity<?> getDetailResume(@RequestHeader("Authorization") String authorizationHeader){
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.getDetailResume(user.getUserId());
    }

}
