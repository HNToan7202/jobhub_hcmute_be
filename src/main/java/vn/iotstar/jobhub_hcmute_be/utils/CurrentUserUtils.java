package vn.iotstar.jobhub_hcmute_be.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.Impl.RedisServiceImpl;

public class CurrentUserUtils {

    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            return authentication.getName();
        } else {
            return "";
        }
    }
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();
            return userDetail.getUserId();
        } else {
            return "";
        }
    }

}
