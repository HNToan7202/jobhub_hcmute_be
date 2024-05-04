package vn.iotstar.jobhub_hcmute_be.service.Impl;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.NewUserDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Role;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DownFileImpl {
    @Autowired
    FileServiceImpl fileService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    public byte[] downloadNewUserList(int page, int size, String startOfDayYesterdayStr, String endOfDayYesterdayStr, String roleStr) {
        try {
            Role role = roleRepository.findByName(roleStr);
            System.out.println(role.getName());
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date startOfDayYesterday = format.parse(startOfDayYesterdayStr );
            Date endOfDayYesterday = format.parse(endOfDayYesterdayStr );
//            LocalDate today = LocalDate.now();
//            LocalDate yesterday = today.minusDays(3);
//
//            Date startOfDayYesterday = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            Date endOfDayYesterday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            System.out.println(startOfDayYesterday);
            System.out.println(endOfDayYesterday);
            String link = "";
            Page<User> users = userRepository.findByRoleAndCreatedAtBetween(role, startOfDayYesterday, endOfDayYesterday, PageRequest.of(page, size));
            System.out.println(users.getContent().size());
            List<User> userList = users.getContent();
            List<NewUserDTO> newUserDTOList = new ArrayList<>();
            for (User user : userList) {
                NewUserDTO newUserDTO = new NewUserDTO();
                newUserDTO.setEmail(user.getEmail());
                newUserDTO.setPhone(user.getPhone());
                newUserDTO.setCreatedAt(user.getCreatedAt());
                newUserDTO.setLastLoginAt(user.getLastLoginAt());
                newUserDTO.setUserId(user.getUserId());
                newUserDTO.setVerified(user.isVerified());
                newUserDTO.setActive(user.getIsActive());
                if (role.getName().equals("STUDENT")) {
                    newUserDTO.setFullName(((Student) user).getFullName());
                    newUserDTO.setAvatar(((Student) user).getAvatar());
                    link = "https://job-hub-hcmute.vercel.app/candidates/";
                } else if (role.getName().equals("EMPLOYER")) {
                    newUserDTO.setFullName(((Employer) user).getCompanyName());
                    newUserDTO.setAvatar(((Employer) user).getLogo());
                    link = "https://job-hub-hcmute.vercel.app/employers/";
                }
                newUserDTOList.add(newUserDTO);
            }
             return fileService.downloadNewUserList(newUserDTOList, link);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }
}
