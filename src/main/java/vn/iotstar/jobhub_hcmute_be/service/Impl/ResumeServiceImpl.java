package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.ResumeDTO;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.repository.ResumeRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.ResumeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public <S extends Resume> List<S> saveAll(Iterable<S> entities) {
        return resumeRepository.saveAll(entities);
    }

    @Override
    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    @Override
    public <S extends Resume> S save(S entity) {
        return resumeRepository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        resumeRepository.deleteById(s);
    }

    @Override
    public void delete(Resume entity) {
        resumeRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        resumeRepository.deleteAll();
    }

    @Override
    public List<Resume> findAll(Sort sort) {
        return resumeRepository.findAll(sort);
    }

    @Override
    public Page<Resume> findAll(Pageable pageable) {
        return resumeRepository.findAll(pageable);
    }


    @Override
    public ResponseEntity<?> uploadResumeFile(MultipartFile resumFile, String userId) throws IOException {
        String url = cloudinaryService.uploadResume(resumFile, userId);
        if(url == null){
            return ResponseEntity.status(500).body(GenericResponse.builder()
                    .success(false)
                    .message("Internal Server Error")
                    .statusCode(500)
                    .build());
        }
        Optional<Student> opt = studentRepository.findById(userId);
        if(!opt.isPresent()){
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("User Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        if (student.getResume() == null){
            Resume resume = new Resume();
            //resume.setStudent(student);
            resume.setCreateAt(new Date());
            resume.setUpdateAt(new Date());
            student.setResume(resume);
        }
        ResumeUpload resume = new ResumeUpload();
        resume.setLinkUpload(url);
        //resume.setCandidate(candidate);
        resume.setName(resumFile.getOriginalFilename());
        resume.setCreateAt(new Date());
        resume.setUpdateAt(new Date());
        List<ResumeUpload> resumeUploads;
        resumeUploads = student.getResume().getResumeUploads();
        if(resumeUploads == null)
            resumeUploads = new ArrayList<>();
        resumeUploads.add(resume);
        student.getResume().setResumeUploads(resumeUploads);
        student = studentRepository.save(student);
        return ResponseEntity.status(200).body(GenericResponse.builder()
                .success(true)
                .message("Upload Resume Successfully!")
                .result(student.getResume().getResumeUploads().get(student.getResume().getResumeUploads().size()-1))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<?> updateResume(ResumeDTO resumeDTO, String studentId){
        Optional<Student> opt = studentRepository.findById(studentId);
        if(!opt.isPresent()){
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("User Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        try {
            Resume resume = student.getResume();
            if(resume == null){
                resume = new Resume();
                resume.setCreateAt(new Date());
                resume.setUpdateAt(new Date());
                student.setResume(resume);
            }
            BeanUtils.copyProperties(resumeDTO, resume);
            //r
            Education education = resumeDTO.getEducation();
            List<Education> educations = resume.getEducations();
            educations.add(education);
            resume.setEducations(educations);

            Experience experience = resumeDTO.getExperience();
            List<Experience> experiences = resume.getExperiences();
            experiences.add(experience);
            resume.setExperiences(experiences);

            Certificate certificate = resumeDTO.getCertificate();
            List<Certificate> certificates = resume.getCertificates();
            certificates.add(certificate);
            resume.setCertificates(certificates);

            Prize prize = resumeDTO.getPrize();
            List<Prize> prizes = resume.getPrizes();
            prizes.add(prize);
            resume.setPrizes(prizes);

            Course course = resumeDTO.getCourse();
            List<Course> courses = resume.getCourses();
            courses.add(course);
            resume.setCourses(courses);

            Project project = resumeDTO.getProject();
            List<Project> projects = resume.getProjects();
            projects.add(project);
            resume.setProjects(projects);

            SocialActivity socialActivity = resumeDTO.getSocialActivity();
            List<SocialActivity> socialActivities = resume.getSocialActivities();
            socialActivities.add(socialActivity);
            resume.setSocialActivities(socialActivities);

            Social social = resumeDTO.getSocial();
            List<Social> socials = resume.getSocials();
            socials.add(social);
            resume.setSocials(socials);

            resume.setUpdateAt(new Date());
            resume.setStudent(student);
            student = studentRepository.save(student);
            return ResponseEntity.status(200).body(GenericResponse.builder()
                    .success(true)
                    .message("Update Resume Successfully!")
                    .result(student.getResume())
                    .statusCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(GenericResponse.builder()
                    .success(false)
                    .message("Internal Server Error")
                    .statusCode(500)
                    .build());
            // Xử lý ngoại lệ nếu cần
        }

    }

    @Override
    public ResponseEntity<?> getDetailResume(String studentId){
        Optional<Student> opt = studentRepository.findById(studentId);

        Student student = opt.get();
        return ResponseEntity.status(200).body(GenericResponse.builder()
                .success(true)
                .message("Get Resume Successfully!")
                .result(student.getResume())
                .statusCode(HttpStatus.OK.value())
                .build());
    }


    @Override
    public ResponseEntity<?> deleteResume(String resumeId, String userId) throws IOException {

        Optional<Student> opt = studentRepository.findById(userId);
        if(!opt.isPresent()){
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("User Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        Resume resume = student.getResume();
        if(resume == null){
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("Resume Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }

        List<ResumeUpload> resumeUploads = resume.getResumeUploads();
        if(resumeUploads == null){
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("Resume Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        //delete(cv);
        return ResponseEntity.status(200)
                .body(
                        GenericResponse.builder()
                                .success(true)
                                .message("Delete Resume Success")
                                .statusCode(200)
                                .build()
                );

    }
}
