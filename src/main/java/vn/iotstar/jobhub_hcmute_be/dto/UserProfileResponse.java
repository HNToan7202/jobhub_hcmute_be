package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.constant.Gender;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserProfileResponse {
    private String userId;
    private String phone;
    private String email;
    private String fullName;
    private String avatar;
    private String address;
    private Date dateOfBirth;
    private String about;
    private Gender gender;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private String role;
    private List<String> skills = new ArrayList<>();
    private boolean blacklist;


    public UserProfileResponse(User user) {
        this.userId = user.getUserId();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
        this.address = user.getAddress();
        this.dateOfBirth = user.getDateOfBirth();
        this.about = user.getAbout();
        this.gender = user.getGender();
        this.isActive = user.isActive();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.role = user.getRole().getName();
        if (user instanceof Student) {
            this.blacklist = ((Student) user).getBlackList() != null;
        }
    }
}
