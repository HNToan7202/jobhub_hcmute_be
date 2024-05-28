package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.CoursesDTO;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.CoursesService;

@RestController
@RequestMapping("/api/v1/course")
@Tag(name = "Course", description = "Course API")
public class CourseController {
    private CoursesService coursesService;
    private ResponseBuild responseBuild;

    public CourseController(CoursesService coursesService, ResponseBuild responseBuild) {
        this.coursesService = coursesService;
        this.responseBuild = responseBuild;
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/get-all")
    public ResponseModel getAllCourses(@RequestParam(defaultValue = "1") int index, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String type) {
        return responseBuild.build(coursesService.getCoursesListAdmin(index, size, type));
    }
    @PreAuthorize("hasAnyRole('EMPLOYER')")
    @GetMapping("/get-all-courses")
    public ResponseModel getAllCoursesEmployer(@RequestParam(defaultValue = "1") int index, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String type){
        return responseBuild.build(coursesService.getCoursesList(index, size, type));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    @PostMapping
    public ResponseModel addCourses(@Valid @RequestBody CoursesDTO coursesDTO) {
        return responseBuild.build(coursesService.addCourses(coursesDTO));
    }
    @PreAuthorize("hasAnyRole('EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseModel updateCourses(@PathVariable String id, @Valid @RequestBody CoursesDTO coursesDTO) {
        return responseBuild.build(coursesService.updateCourses(id, coursesDTO));
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseModel updateCoursesAdmin(@PathVariable String id, @Valid @RequestBody CoursesDTO coursesDTO) {
        return responseBuild.build(coursesService.updateCoursesAdmin(id, coursesDTO));
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseModel deleteCourses(@PathVariable String id) {
        return responseBuild.build(coursesService.deleteCourses(id));
    }
    @PreAuthorize("hasAnyRole('EMPLOYER')")
    @DeleteMapping("/employer/{id}")
    public ResponseModel deleteCoursesEmployer(@PathVariable String id) {
        return responseBuild.build(coursesService.deleteCoursesEmployer(id));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    @PutMapping("/change-status/{id}")
    public ResponseModel changeStatusCourses(@PathVariable String id) {
        return responseBuild.build(coursesService.changeStatusCourses(id));
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/change-active/{id}")
    public ResponseModel changeActiveCourses(@PathVariable String id) {
        return responseBuild.build(coursesService.changeActiveCourses(id));
    }





}
