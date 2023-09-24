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
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.repository.*;
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

    @Autowired
    EducationRepository educationRepository;

    @Autowired
    SocialActivityRepository socialActivityRepository;

    @Autowired
    SocialRepository socialRepository;

    @Autowired
    ExperienceRepository experienceRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    PrizeRepository prizeRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ProjectRepository projectRepository;

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
        if (url == null) {
            return ResponseEntity.status(500).body(GenericResponse.builder()
                    .success(false)
                    .message("Internal Server Error")
                    .statusCode(500)
                    .build());
        }
        Optional<Student> opt = studentRepository.findById(userId);
        if (!opt.isPresent()) {
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("User Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        if (student.getResume() == null) {
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
        if (resumeUploads == null)
            resumeUploads = new ArrayList<>();
        resumeUploads.add(resume);
        student.getResume().setResumeUploads(resumeUploads);
        student = studentRepository.save(student);
        return ResponseEntity.status(200).body(GenericResponse.builder()
                .success(true)
                .message("Upload Resume Successfully!")
                .result(student.getResume().getResumeUploads().get(student.getResume().getResumeUploads().size() - 1))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    public Resume convertResumeDtoToResume(ResumeDTO resumeDTO, Resume resume1) {
        Resume resume = new Resume();
        List<Education> educations = new ArrayList<>();
        //List<Education> educationsUpdate = new ArrayList<>();
        for (EducationDTO resu : resumeDTO.getEducations()) {
            Education education = new Education();
            if (resu.getId() != null) {

            }
        }
        List<Experience> experiences = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getExperiences().size(); i++) {
            Experience experience = new Experience();
            BeanUtils.copyProperties(resumeDTO.getExperiences().get(i), experience);
            experience.setResume(resume1);
            experiences.add(experience);
        }
        List<Certificate> certificates = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getCertificates().size(); i++) {
            Certificate certificate = new Certificate();
            BeanUtils.copyProperties(resumeDTO.getCertificates().get(i), certificate);
            certificate.setResume(resume1);
            certificates.add(certificate);
        }
        List<Prize> prizes = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getPrizes().size(); i++) {
            Prize prize = new Prize();
            BeanUtils.copyProperties(resumeDTO.getPrizes().get(i), prize);
            prize.setResume(resume1);
            prizes.add(prize);
        }
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getCourses().size(); i++) {
            Course course = new Course();
            BeanUtils.copyProperties(resumeDTO.getCourses().get(i), course);
            course.setResume(resume1);
            courses.add(course);
        }
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getProjects().size(); i++) {
            Project project = new Project();
            BeanUtils.copyProperties(resumeDTO.getProjects().get(i), project);
            project.setResume(resume1);
            projects.add(project);
        }
        List<SocialActivity> socialActivities = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getSocialActivities().size(); i++) {
            SocialActivity socialActivity = new SocialActivity();
            BeanUtils.copyProperties(resumeDTO.getSocialActivities().get(i), socialActivity);
            socialActivity.setResume(resume1);
            socialActivities.add(socialActivity);
        }
        List<Social> socials = new ArrayList<>();
        for (int i = 0; i < resumeDTO.getSocials().size(); i++) {
            Social social = new Social();
            BeanUtils.copyProperties(resumeDTO.getSocials().get(i), social);
            social.setResume(resume1);
            socials.add(social);
        }
        resume.setEducations(educations);
        resume.setExperiences(experiences);
        resume.setCertificates(certificates);
        resume.setPrizes(prizes);
        resume.setCourses(courses);
        resume.setProjects(projects);
        resume.setSocialActivities(socialActivities);
        resume.setSocials(socials);
        resume.setCreateAt(new Date());
        resume.setUpdateAt(new Date());
        return resume;
    }

    @Override
    public ResponseEntity<?> updateResume(ResumeDTO resumeDTO, String studentId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (!opt.isPresent()) {
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("Student Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        try {
            Resume resume = student.getResume();
            if (resume == null) {
                resume = new Resume();
                resume.setCreateAt(new Date());
                resume.setUpdateAt(new Date());
                student.setResume(resume);
            }
            System.out.println("resumeDTO: " + resumeDTO.getIsEducationsEdited());
            if (resumeDTO.getIsEducationsEdited() == true) {
                List<Education> educations = new ArrayList<>();
                for (EducationDTO educationDTO : resumeDTO.getEducations()) {
                    Education education = new Education();
                    if (educationDTO.getId() != null && educationDTO.getIsEdit() == true) {
                        Optional<Education> optEducation = educationRepository.findById(educationDTO.getId());
                        if (!optEducation.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Education Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        education = optEducation.get();
                        //BeanUtils.copyProperties(educationDTO, education);
                        education.setTitle(educationDTO.getTitle());
                        education.setLocation(educationDTO.getLocation());
                        education.setYearBegin(educationDTO.getYearBegin());
                        education.setYearEnd(educationDTO.getYearEnd());
                        education.setDescription(educationDTO.getDescription());

                        education = educationRepository.save(education);
                        System.out.println("education: " + resume.getEducations().toString());
                    } else if(educationDTO.getId() == null && educationDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(educationDTO, education);
                        education.setResume(resume);
                        educations.add(education);
                    }
                    else if(educationDTO.getId()!= null && educationDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(educationDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Education not found");
                    }
                }

                List<Education> educationsOld = resume.getEducations();
                educationsOld.addAll(educations);
                resume.setEducations(educationsOld);

            }
            if (resumeDTO.getIsCoursesEdited()) {
                List<Course> courses = new ArrayList<>();
                for (CourseDTO courseDTO : resumeDTO.getCourses()) {
                    Course course = new Course();
                    if (courseDTO.getId() != null && courseDTO.getIsEdit() == true) {
                        Optional<Course> optCourse = courseRepository.findById(courseDTO.getId());
                        if (!optCourse.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Course Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        course = optCourse.get();
                        BeanUtils.copyProperties(courseDTO, course);
                        course = courseRepository.save(course);
                    } else if (courseDTO.getId() == null && courseDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(courseDTO, course);
                        course.setResume(resume);
                        courses.add(course);
                    }
                    if(courseDTO.getId()!= null && courseDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(courseDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Education not found");
                    }

                }
                List<Course> coursesOld = resume.getCourses();
                coursesOld.addAll(courses);
                resume.setCourses(coursesOld);
            }

            if (resumeDTO.getIsCertificatesEdited()) {
                List<Certificate> certificates = new ArrayList<>();
                for (CertificateDTO certificateDTO : resumeDTO.getCertificates()) {
                    Certificate certificate = new Certificate();
                    if (certificateDTO.getId() != null && certificateDTO.getIsEdit() == true) {
                        Optional<Certificate> optCertificate = certificateRepository.findById(certificateDTO.getId());
                        if (!optCertificate.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Certificate Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        certificate = optCertificate.get();
                        BeanUtils.copyProperties(certificateDTO, certificate);
                        certificate = certificateRepository.save(certificate);
                    } else if (certificateDTO.getId() == null && certificateDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(certificateDTO, certificate);
                        certificate.setResume(resume);
                        certificates.add(certificate);
                    }
                     if(certificateDTO.getId()!= null && certificateDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(certificate.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Education not found");
                    }

                }
                List<Certificate> certificatesOld = resume.getCertificates();
                certificatesOld.addAll(certificates);
                resume.setCertificates(certificatesOld);
            }

            if (resumeDTO.getIsExperiencesEdited()) {
                List<Experience> experiences = new ArrayList<>();
                for (ExperienceDTO experienceDTO : resumeDTO.getExperiences()) {
                    Experience experience = new Experience();
                    if (experienceDTO.getId() != null && experienceDTO.getIsEdit() == true) {
                        Optional<Experience> optExperience = experienceRepository.findById(experienceDTO.getId());
                        if (!optExperience.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Experience Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        experience = optExperience.get();
                        BeanUtils.copyProperties(experienceDTO, experience);
                        experience = experienceRepository.save(experience);
                    } else if (experienceDTO.getId() == null && experienceDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(experienceDTO, experience);
                        experience.setResume(resume);
                        experiences.add(experience);
                    }
                    if(experience.getId()!= null && experienceDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(experienceDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Experience not found");
                    }

                }
                List<Experience> experiencesOld = resume.getExperiences();
                experiencesOld.addAll(experiences);
                resume.setExperiences(experiencesOld);
            }
            if (resumeDTO.getIsPrizesEdited()) {
                List<Prize> prizes = new ArrayList<>();
                for (PrizeDTO prizeDTO : resumeDTO.getPrizes()) {
                    Prize prize = new Prize();
                    if (prizeDTO.getId() != null && prizeDTO.getIsEdit() == true) {
                        Optional<Prize> optPrize = prizeRepository.findById(prizeDTO.getId());
                        if (!optPrize.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Prize Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        prize = optPrize.get();
                        BeanUtils.copyProperties(prizeDTO, prize);
                        prize = prizeRepository.save(prize);
                    } else if (prizeDTO.getId() == null && prizeDTO.getIsEdit() == false) {
                        BeanUtils.copyProperties(prizeDTO, prize);
                        prize.setResume(resume);
                        prizes.add(prize);
                    }
                    if(prizeDTO.getId()!= null && prizeDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(prizeDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Prize not found");
                    }

                }
                List<Prize> prizesOld = resume.getPrizes();
                prizesOld.addAll(prizes);
                resume.setPrizes(prizesOld);
            }
            if (resumeDTO.getIsProjectsEdited()) {
                List<Project> projects = new ArrayList<>();
                for (ProjectDTO projectDTO : resumeDTO.getProjects()) {
                    Project project = new Project();
                    if (projectDTO.getId() != null && projectDTO.getIsEdit() == true) {
                        Optional<Project> optProject = projectRepository.findById(projectDTO.getId());
                        if (!optProject.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Project Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        project = optProject.get();
                        BeanUtils.copyProperties(projectDTO, project);
                        project = projectRepository.save(project);
                    } else if (projectDTO.getId() == null && projectDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(projectDTO, project);
                        project.setResume(resume);
                        projects.add(project);
                    }
                    if(projectDTO.getId()!= null && projectDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(projectDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Project not found");
                    }

                }
                List<Project> projectsOld = resume.getProjects();
                projectsOld.addAll(projects);
                resume.setProjects(projectsOld);
            }

            if (resumeDTO.getIsSocialActivitiesEdited()) {
                List<SocialActivity> socialActivities = new ArrayList<>();
                for (SocialActivityDTO socialActivityDTO : resumeDTO.getSocialActivities()) {
                    SocialActivity socialActivity = new SocialActivity();
                    if (socialActivityDTO.getId() != null && socialActivityDTO.getIsEdit() == true) {
                        Optional<SocialActivity> optSocialActivity = socialActivityRepository.findById(socialActivityDTO.getId());
                        if (!optSocialActivity.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Social Activity Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        socialActivity = optSocialActivity.get();
                        BeanUtils.copyProperties(socialActivityDTO, socialActivity);
                        socialActivity = socialActivityRepository.save(socialActivity);
                    } else if (socialActivityDTO.getId() == null && socialActivityDTO.getIsEdit() == false){
                        BeanUtils.copyProperties(socialActivityDTO, socialActivity);
                        socialActivity.setResume(resume);
                        socialActivities.add(socialActivity);
                    }
                    if(socialActivityDTO.getId()!= null && socialActivityDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(socialActivityDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Education not found");
                    }

                }
                List<SocialActivity> socialActivitiesOld = resume.getSocialActivities();
                socialActivitiesOld.addAll(socialActivities);
                resume.setSocialActivities(socialActivitiesOld);
            }

            if (resumeDTO.getIsSocialsEdited()) {
                List<Social> socials = new ArrayList<>();
                for (SocialDTO socialDTO : resumeDTO.getSocials()) {
                    Social social = new Social();
                    if (socialDTO.getId() != null && socialDTO.getIsEdit() == true) {
                        Optional<Social> optSocial = socialRepository.findById(socialDTO.getId());
                        if (!optSocial.isPresent()) {
                            return ResponseEntity.status(404).body(GenericResponse.builder()
                                    .success(false)
                                    .message("Social Not Found")
                                    .statusCode(HttpStatus.NOT_FOUND.value())
                                    .build());
                        }
                        social = optSocial.get();
                        BeanUtils.copyProperties(socialDTO, social);
                        social = socialRepository.save(social);
                    } else if (socialDTO.getId() == null && socialDTO.getIsEdit() == false) {
                        BeanUtils.copyProperties(socialDTO, social);
                        social.setResume(resume);
                        socials.add(social);
                    }
                    if(socialDTO.getId()!= null && socialDTO.getIsDelete() == true){
                        Optional<Education> optionalEducation = educationRepository.findById(socialDTO.getId());
                        if(optionalEducation.isPresent()){
                            educationRepository.deleteById(optionalEducation.get().getId());
                        }
                        else throw new RuntimeException("Education not found");
                    }
                }
                List<Social> socialsOld = resume.getSocials();
                socialsOld.addAll(socials);
                resume.setSocials(socialsOld);
            }


            //Resume resume1 = convertResumeDtoToResume(resumeDTO, resume);
            //BeanUtils.copyProperties(resume1, resume);

            resume.setUpdateAt(new Date());
            resume.setStudent(student);

            //student = studentRepository.save(student);
            resume = resumeRepository.save(resume);
            return ResponseEntity.status(200).body(GenericResponse.builder()
                    .success(true)
                    .message("Update Resume Successfully!")
                    .result(resume)
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
    public ResponseEntity<?> getDetailResume(String studentId) {
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
        if (!opt.isPresent()) {
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("User Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }
        Student student = opt.get();
        Resume resume = student.getResume();
        if (resume == null) {
            return ResponseEntity.status(404).body(GenericResponse.builder()
                    .success(false)
                    .message("Resume Not Found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        }

        List<ResumeUpload> resumeUploads = resume.getResumeUploads();
        if (resumeUploads == null) {
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
