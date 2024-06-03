package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Courses;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;

public interface CoursesRepository extends JpaRepository<Courses, String> {

    Page<Courses> findAllByEmployerAndTypeOrderByCreatedAtDesc(Employer employer, String type, PageRequest of);

    Page<Courses> findAllByEmployerOrderByCreatedAtDesc(Employer employer, PageRequest of);

    Page<Courses> findAllByTypeOrderByCreatedAtDesc(String type, PageRequest of);

    Page<Courses> findAllByOrderByCreatedAtDesc(PageRequest of);

    Page<Courses> findAllByStatusAndActiveOrderByCreatedAtDesc(boolean b, boolean b1, PageRequest of);


    Page<Courses> findAllByTypeAndStatusAndActiveOrderByCreatedAtDesc(String type, boolean status, boolean active, PageRequest of);
}