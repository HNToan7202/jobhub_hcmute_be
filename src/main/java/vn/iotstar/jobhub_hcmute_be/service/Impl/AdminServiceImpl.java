package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.repository.AdminRepository;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.AdminService;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
   @Autowired
   private AdminRepository adminRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
    EmployerRepository employerRepository;

    @Override
    @Deprecated
    public Admin getOne(String s) {
        return adminRepository.getOne(s);
    }

    @Override
    public <S extends Admin> List<S> findAll(Example<S> example) {
        return adminRepository.findAll(example);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public <S extends Admin> S save(S entity) {
        return adminRepository.save(entity);
    }

    @Override
    public Optional<Admin> findById(String s) {
        return adminRepository.findById(s);
    }

    @Override
    public void deleteById(String s) {
        adminRepository.deleteById(s);
    }

    @Override
    public ResponseEntity<?> acceptEmployer(String employerId){
        Optional<Employer> optionalUserser = employerRepository.findById(employerId);
        if(optionalUserser.isPresent()){
            if(optionalUserser.get().getEmployState() == EmployState.ACTIVE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Employer is already active!")
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build());
            }
            Employer employer = optionalUserser.get();
            employer.setEmployState(EmployState.ACTIVE);
            employerRepository.save(employer);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Accept Employer Successful!")
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Fail to read request!")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

}
