//package vn.iotstar.jobhub_hcmute_be.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import vn.iotstar.jobhub_hcmute_be.model.SessionModel;
//import vn.iotstar.jobhub_hcmute_be.service.Impl.RedisServiceImpl;
//import org.springframework.security.core.Authentication;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class AuthenticationManagerWithRedis extends RedisServiceImpl {
//
//    @Autowired
//    AuthenticationManager authenticationManager;
//    public AuthenticationManagerWithRedis(RedisTemplate<String, Object> redisTemplate) {
//        super(redisTemplate);
//    }
//
//    public Collection<? extends GrantedAuthority> getAuthorities(List<String> permissions, String role) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        for(String permission:permissions){
//            authorities.add(new SimpleGrantedAuthority(permission));
//        }
//        authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
//        return authorities;
//    }
//    @PostConstruct
//    public void init() {
//        checkAuthenticationAfterServerRestart();
//    }
//    public void checkAuthenticationAfterServerRestart() {
//        Set<String> sessionKeys = this.getKeysWithPrefix("session:");
//        for (String key : sessionKeys) {
//            SessionModel user = this.getSession(key);
//            if (user != null) {
//                UsernamePasswordAuthenticationToken authRequest =
//                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword() , getAuthorities(user.getPermissionsName(), user.getNameRole()));
//                Authentication authentication = authenticationManager.authenticate(authRequest);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//    }
//
//}
//
