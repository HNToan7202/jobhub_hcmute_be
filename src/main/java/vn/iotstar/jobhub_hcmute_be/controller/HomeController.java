package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.repository.AdminRepository;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;
import vn.iotstar.jobhub_hcmute_be.service.EventService;
import vn.iotstar.jobhub_hcmute_be.service.PositionService;

@RestController
@Tag(name="Home", description="Home API")
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    AdminRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;


    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    EmployerService employerService;

    @Autowired
    PositionService positionService;

    @Autowired
    EventService eventService;

    @GetMapping("/")
    public String helloWorld(){
        return "Hello World, This is Group 8!";
    }
    @GetMapping("create-admin")
    public String createAdmin(){
        Admin admin = new Admin();
        admin.setFullName("Admin");
        admin.setIsActive(true);
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("28072002Thanh@"));
        admin.setRole(roleRepository.findByName("ADMIN"));
        admin = repository.save(admin);
        return "Create admin success";
    }
    @GetMapping("top-company")
    public ResponseModel topCompanyRegister(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "index", required = false, defaultValue = "0") int index){
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = employerService.topCompany(PageRequest.of(index, size));
        }
        catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("position/popular")
    public ResponseModel topCateJob(){
        ActionResult actionResult = new ActionResult();
        try{
            actionResult = positionService.topPopularJobByPosition();

        }catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }

        return responseBuild.build(actionResult);
    }
    @GetMapping("event/get-list")
    public ResponseModel getListEvent(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "index", required = false, defaultValue = "0") int index){
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = eventService.getAllEvent(PageRequest.of(index, size));
        }
        catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }
}
