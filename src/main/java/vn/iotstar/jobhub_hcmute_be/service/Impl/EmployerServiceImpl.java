package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerUpdateDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Override
    public <S extends Employer> S save(S entity) {
        return employerRepository.save(entity);
    }

    @Override
    public Optional<Employer> findById(String s) {
        return employerRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return employerRepository.existsById(s);
    }

    @Override
    public long count() {
        return employerRepository.count();
    }

    @Override
    public void deleteById(String s) {
        employerRepository.deleteById(s);
    }

    @Override
    public void delete(Employer entity) {
        employerRepository.delete(entity);
    }

    @Override
    public <S extends Employer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return employerRepository.findBy(example, queryFunction);
    }

    @Override
    public ResponseEntity<GenericResponse> changeLogo(String userId, MultipartFile imageFile) throws IOException {
        Employer user = findById(userId).get();
        String avatarOld = user.getLogo();

        //upload new avatar
        user.setLogo(cloudinaryService.uploadImage(imageFile));
        save(user);

        //delete old avatar
        if (avatarOld != null) {
            cloudinaryService.deleteImage(avatarOld);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully").result(user.getLogo()).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateCompanyProfile(String userId, EmployerUpdateDTO request) throws Exception {
        Optional<Employer> employerOptional = findById(userId);
        String phone = request.getPhone();
        if (employerOptional.isEmpty()) throw new Exception("User doesn't exist");

        if(!phone.isEmpty()){
            Optional<Employer> optional = employerRepository.findByPhoneAndIsActiveIsTrue(request.getPhone());
            if(optional.isPresent() && !optional.get().getUserId().equals(userId))
                throw new Exception("Phone number already in use");
        }

        Employer employer = employerOptional.get();
        BeanUtils.copyProperties(request, employer);

        employer = save(employer);

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Update successful")
                        .result(employer)
                        .statusCode(200)
                        .build()
        );
    }


}
