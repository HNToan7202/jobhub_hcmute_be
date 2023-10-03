package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserUpdateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.StudentService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> findAllById(Iterable<String> strings) {
        return studentRepository.findAllById(strings);
    }

    @Override
    public <S extends Student> S save(S entity) {
        return studentRepository.save(entity);
    }

    @Override
    public Optional<Student> findById(String s) {
        return studentRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return studentRepository.existsById(s);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }

    @Override
    public void deleteById(String s) {
        studentRepository.deleteById(s);
    }

    @Override
    public void delete(Student entity) {
        studentRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        studentRepository.deleteAll();
    }

    @Override
    public ResponseEntity<GenericResponse> changeAvatar(String userId, MultipartFile imageFile) throws IOException {

        Student user = findById(userId).get();
        String avatarOld = user.getAvatar();

        //upload new avatar
        user.setAvatar(cloudinaryService.uploadImage(imageFile));
        save(user);

        //delete old avatar
        if (avatarOld != null) {
            cloudinaryService.deleteImage(avatarOld);
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Upload successfully")
                .result(user.getAvatar())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateProfile(String userId, UserUpdateRequest request) throws Exception {
        Optional<Student> user = findById(userId);
        String phone =  request.getPhone();
        if (user.isEmpty())
            throw new Exception("User doesn't exist");

//        if (request.getDateOfBirth().after(new Date()))
//            throw new Exception("Invalid date of birth");

        //Kiểm tra format của số điện thoại đúng định dạng của việt nam
//        if (!request.getPhone().matches("^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})$"))
//            throw new Exception("Invalid phone number");
        //Chỉ 1 định dạng duy nhất là 84 không có số 0 -> nhập 0 -> 84
//       if (request.getPhone().startsWith("0"))
//            phone = "84" + request.getPhone().substring(1);
        if(!phone.isEmpty()){
            Optional<Student> optional = studentRepository.findByPhoneAndIsActiveIsTrue(phone);
            if(optional.isPresent() && !optional.get().getUserId().equals(userId))
                throw new Exception("Phone number already in use");
        }

        user.get().setGender(Gender.valueOf(String.valueOf(request.getGender())));
        user.get().setPhone(phone);
        user.get().setFullName(request.getFullName());
        user.get().setDateOfBirth(request.getDateOfBirth());
        user.get().setAddress(request.getAddress());
        user.get().setAbout(request.getAbout());
        save(user.get());

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Update successful")
                        .result(user.get())
                        .statusCode(200)
                        .build()
        );
    }
}
