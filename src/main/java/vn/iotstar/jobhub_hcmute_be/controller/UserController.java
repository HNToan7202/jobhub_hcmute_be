package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.UserProfileResponse;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name="User", description="User API")
public class UserController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    @Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    //@SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GenericResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            GenericResponse.builder()
                                    .success(false)
                                    .message("Unauthorized: Empty or invalid token")
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .build()
                    );
        }
        try {
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();

            return ResponseEntity.ok(
                    GenericResponse.builder()
                            .success(true)
                            .message("Retrieving user profile successfully")
                            .result(new UserProfileResponse(userDetail.getUser()))
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );
        } catch (ClassCastException e) {
            throw new UsernameNotFoundException("Invalid user principal type", e);
        }
    }

}
