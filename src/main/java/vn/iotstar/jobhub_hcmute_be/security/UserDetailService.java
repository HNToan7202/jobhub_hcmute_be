package vn.iotstar.jobhub_hcmute_be.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Role;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        User user = null;
        if(userRepository.findByEmailAndIsActiveIsTrue(emailOrPhone).isPresent()){
            user = userRepository.findByEmailAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(()->new UsernameNotFoundException("user is not found"));
        }else {
            user = userRepository.findByPhoneAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(()->new UsernameNotFoundException("user is not found"));
        }
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        return userDetail;
    }

    public UserDetails loadUserByUserId(String id){
        User user = userRepository.findByUserIdAndIsActiveIsTrue(id)
                .orElseThrow(()->new UsernameNotFoundException("user is not found"));
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        return userDetail;
    }


}
