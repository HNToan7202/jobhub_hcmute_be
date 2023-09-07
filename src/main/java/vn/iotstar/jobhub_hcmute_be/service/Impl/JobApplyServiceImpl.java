package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.entity.ResumeUpload;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.repository.JobApplyRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JobApplyServiceImpl implements JobApplyService {

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public <S extends JobApply> List<S> saveAll(Iterable<S> entities) {
        return jobApplyRepository.saveAll(entities);
    }

    @Override
    public List<JobApply> findAll() {
        return jobApplyRepository.findAll();
    }

    @Override
    public List<JobApply> findAllById(Iterable<String> strings) {
        return jobApplyRepository.findAllById(strings);
    }

    @Override
    public <S extends JobApply> S save(S entity) {
        return jobApplyRepository.save(entity);
    }

    @Override
    public Optional<JobApply> findById(String s) {
        return jobApplyRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return jobApplyRepository.existsById(s);
    }

    @Override
    public long count() {
        return jobApplyRepository.count();
    }

    @Override
    public void deleteById(String s) {
        jobApplyRepository.deleteById(s);
    }

    @Override
    public void delete(JobApply entity) {
        jobApplyRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        jobApplyRepository.deleteAllById(strings);
    }

    @Override
    public void deleteAll(Iterable<? extends JobApply> entities) {
        jobApplyRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        jobApplyRepository.deleteAll();
    }

    @Override
    public List<JobApply> findAll(Sort sort) {
        return jobApplyRepository.findAll(sort);
    }

    @Override
    public Page<JobApply> findAll(Pageable pageable) {
        return jobApplyRepository.findAll(pageable);
    }

    @Override
    public <S extends JobApply> Page<S> findAll(Example<S> example, Pageable pageable) {
        return jobApplyRepository.findAll(example, pageable);
    }

    @Override
    public <S extends JobApply> long count(Example<S> example) {
        return jobApplyRepository.count(example);
    }

    @Override
    public <S extends JobApply, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return jobApplyRepository.findBy(example, queryFunction);
    }

    @Override
    public ResponseEntity<?> applyForJob(String userId, String jobId, String resumeLink) {

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            throw new NotFoundException("Job not found");
        }
        Job job = optionalJob.get();
        // Kiểm tra xem công việc còn hạn apply hay không
        Date expirationDate = job.getDeadline();
        Date currentDate = new Date();
        if (job.getIsActive() && expirationDate.before(currentDate)) {
            // Nếu công việc đang active nhưng đã hết hạn apply, set trạng thái isActive thành false
            job.setIsActive(false);
            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Job application has expired");
        } else if (!job.getIsActive()) {
            // Nếu công việc đã không còn active, trả về thông báo hết hạn với status code 410 (Gone)
            return ResponseEntity.status(HttpStatus.GONE).body("Job application has expired");
        }

        // Tìm ứng viên trong cơ sở dữ liệu
        Optional<Student> optionalCandidate = studentRepository.findById(userId);
        if (!optionalCandidate.isPresent()) {
            throw new NotFoundException("Candidate not found");
        }
        //Check xem ứng viên đã apply vào job hay chưa
        Student candidate = optionalCandidate.get();
        if(jobApplyRepository.findByStudentAndJob(candidate,job) != null){
            return ResponseEntity.status(409)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Candidate has already applied to this job.")
                            .statusCode(409)
                            .build());
        }

        List<ResumeUpload> resumeUploadList = optionalCandidate.get().getResume().getResumeUploads();
        for (ResumeUpload resumeUpload : resumeUploadList) {
            if(resumeUpload.getLinkUpload().equals(resumeLink)){
                return ResponseEntity.status(409)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Cannot Found CV Link.")
                                .statusCode(409)
                                .build());
            }
        }

//        // Kiểm tra xem CV có tồn tại trong database không
//        Optional<ResumeUpload> optionalResume = resumeRepository.findById(resumeId);
//        if (!optionalResume.isPresent()) {
//            throw new NotFoundException("Resume not found");
//        }
//
//        Resume resume = optionalResume.get();
//        // Tạo đơn ứng tuyển
        JobApply application = new JobApply();
        application.setJob(job);
        application.setState(State.NOT_RECEIVED);
        application.setStudent(candidate);
        application.setResumeUpoad(resumeLink);
        //Ánh xạ những thứ còn lại của candidate qua cho JobApply
        BeanUtils.copyProperties(candidate, application);
        // Lưu đơn ứng tuyển vào database
        JobApply jobApply = jobApplyRepository.save(application);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("Apply Successful!")
                        .result(jobApply)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
