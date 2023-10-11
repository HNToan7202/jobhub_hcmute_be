package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;
import vn.iotstar.jobhub_hcmute_be.dto.CompanyDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.JobDTO;
import vn.iotstar.jobhub_hcmute_be.dto.PostJobRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.PositionRepository;
import vn.iotstar.jobhub_hcmute_be.repository.SkillRepository;
import vn.iotstar.jobhub_hcmute_be.service.JobService;

import java.util.*;

@Service
@Transactional
public class JobServiceImpl implements JobService {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    SkillRepository skillRepository;

    @Scheduled(cron = "0 0 0 * * *") // Chạy mỗi ngày lúc 00:00:00
    public void checkJobDeadlines() {
        // Lấy danh sách tất cả công việc từ cơ sở dữ liệu
        Iterable<Job> jobs = jobRepository.findAll();

        // Lặp qua từng công việc và kiểm tra thời hạn
        for (Job job : jobs) {
            if (job.getDeadline() != null && job.getDeadline().before(new Date())) {
                // Nếu thời hạn đã qua, cập nhật trạng thái isActive thành false
                job.setIsActive(false);
                jobRepository.save(job); // Lưu công việc đã cập nhật
            }
        }
    }


    @Override
    public <S extends Job> List<S> saveAll(Iterable<S> entities) {
        return jobRepository.saveAll(entities);
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public <S extends Job> S save(S entity) {
        return jobRepository.save(entity);
    }

    @Override
    public Optional<Job> findById(String s) {
        return jobRepository.findById(s);
    }

    @Override
    public long count() {
        return jobRepository.count();
    }

    @Override
    public void deleteById(String s) {
        jobRepository.deleteById(s);
    }

    @Override
    public void delete(Job entity) {
        jobRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        jobRepository.deleteAll();
    }

    @Override
    public List<Job> findAll(Sort sort) {
        return jobRepository.findAll(sort);
    }

    @Override
    public Page<Job> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Override
    public ResponseEntity<?> getDetail(String jobId){
        try {
            Optional<Job> optional = jobRepository.findById(jobId);

            if (optional.isPresent()) {
                JobDTO jobDTO = new JobDTO();
                BeanUtils.copyProperties(optional.get(), jobDTO);
                CompanyDTO companyDTO = new CompanyDTO();
                BeanUtils.copyProperties(optional.get().getEmployer(), companyDTO);
                jobDTO.setCompany(companyDTO);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(GenericResponse.builder()
                                .success(true)
                                .message("Get job detail successfully!")
                                .result(jobDTO)
                                .statusCode(HttpStatus.OK.value())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Job not found")
                                .result("Job not found")
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Failed to get job detail")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> getDetail(String jobId, String userId){
        try {
            Optional<Job> optional = jobRepository.findById(jobId);

            if (optional.isPresent()) {
                Job job = optional.get();
                // kiểm tra xem userId đã apply vao job hay chưa
                boolean isApplied = job.getJobApplies().stream().anyMatch(jobApply -> jobApply.getStudent().getUserId().equals(userId));
                JobDTO jobDTO = new JobDTO();
                BeanUtils.copyProperties(optional.get(), jobDTO);
                jobDTO.setIsApplied(isApplied);
                CompanyDTO companyDTO = new CompanyDTO();
                BeanUtils.copyProperties(optional.get().getEmployer(), companyDTO);
                jobDTO.setCompany(companyDTO);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(GenericResponse.builder()
                                .success(true)
                                .message("Get job detail successfully!")
                                .result(jobDTO)
                                .statusCode(HttpStatus.OK.value())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Job not found")
                                .result("Job not found")
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Failed to get job detail")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> findAllByEmployer(String id, Pageable pageable) {

        try {
            Page<Job> jobs =  jobRepository.findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(id, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobs.getContent());
            response.put("currentPage", jobs.getNumber());
            response.put("totalItems", jobs.getTotalElements());
            response.put("totalPages", jobs.getTotalPages());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Get jobs by employer successfully!")
                            .result(response)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Failed to get jobs")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
    public List<JobDTO> convertJobToJobDTO(List<Job> jobs){
        List<JobDTO> jobDTOs = new ArrayList<>();
        for(Job job : jobs){
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            CompanyDTO companyDTO = new CompanyDTO();
            BeanUtils.copyProperties(job.getEmployer(), companyDTO);
            jobDTO.setCompany(companyDTO);
            jobDTOs.add(jobDTO);
        }
        return jobDTOs;
    }

    @Override
    public Page<Job> findAllByIsActiveIsTrueOrderByCreatedAtDesc(Boolean isActive, Pageable pageable){
        return jobRepository.findByIsActiveOrderByCreatedAtDesc(isActive,pageable);
    }

    @Override
    public List<Job> findAllByIsActive(Boolean isActive){
        return jobRepository.findJobsByIsActive(isActive);
    }
    @Override
    public ResponseEntity<GenericResponse> getAlls(Boolean isActive) {



        List<Job> jobs = findAllByIsActive(isActive);

        List<JobDTO> jobDTOs = convertJobToJobDTO(jobs);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobDTOs);
        response.put("total", jobDTOs.size());

        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .message("Get jobs successfully!")
                        .result(response)
                        .statusCode(HttpStatus.OK.value())
                        .build());

    }

    @Override
    public ResponseEntity<GenericResponse> getAllJobs(Pageable pageable, Boolean isActive){

            Page<Job> jobs = findAllByIsActiveIsTrueOrderByCreatedAtDesc(isActive, pageable);
            List<Job> jobList = jobs.getContent();
            List<JobDTO> jobDTOs = convertJobToJobDTO(jobList);
            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobDTOs);
            response.put("currentPage", jobs.getNumber());
            response.put("totalItems", jobs.getTotalElements());
            response.put("totalPages", jobs.getTotalPages());
            return ResponseEntity.status(200)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Get all jobs successfully!")
                            .result(response)
                            .statusCode(200)
                            .build());

    }

    @Override
    public ResponseEntity<?> postJob(PostJobRequest jobRequest, String employerId) {
        try {

            Job job = new Job();
            job.setName(jobRequest.getName());
            job.setJobType(JobType.valueOf(jobRequest.getJobType()));
            job.setQuantity(jobRequest.getQuantity());
            //job.setJobTitle(jobRequest.getJobTitle());
            job.setBenefit(jobRequest.getBenefit());
            job.setSalaryRange(jobRequest.getSalaryRange());
            job.setRequirement(jobRequest.getRequirement());
            job.setLocation(jobRequest.getLocation());
            job.setDescription(jobRequest.getDescription());
            job.setDeadline(jobRequest.getDeadline());
            job.setTime(jobRequest.getTime());
            job.setLink(jobRequest.getLink());

            //Check trước dùng beanutils để ánh xạ
//            Position position = new Position();
//            position.setName(jobRequest.getPositionName());
//            job.setPosition(position);

            // Check if position is existed
            Optional<Position> position = positionRepository.findByName(jobRequest.getPositionName());
            if (position.isPresent()) {
                job.setPosition(position.get());
            } else {
                Position newPosition = new Position();
                newPosition.setName(jobRequest.getPositionName());
                //newPosition = positionRepository.save(newPosition);
                job.setPosition(newPosition);
            }
            List<Skill> skills = new ArrayList<>();

            for (String skillName : jobRequest.getSkillsRequired()) {
                Optional<Skill> skillOptional = skillRepository.findSkillBySkillName(skillName);
                if (skillOptional.isPresent()) {
                    Skill skill;
                    skill = skillOptional.get();
                    skills.add(skill);
                } else {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    //newSkill = skillRepository.save(newSkill);
                    skills.add(newSkill);
                }
            }
            job.setSkills(skills);
            job.setIsActive(true);
            Optional<Employer> optional = employerRepository.findById(employerId);
            if (optional.isPresent()) {
                job.setEmployer(optional.get());
                job.setLogo(optional.get().getLogo());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Unauthorized")
                                .result("Invalid token")
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
            job = jobRepository.save(job);
//            JobDTO jobResponse = new JobDTO();
//            BeanUtils.copyProperties(job, jobResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Post job successfully!")
                            .result(job)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .result("Failed to add job")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
