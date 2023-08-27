package vn.iotstar.jobhub_hcmute_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.LoginDTO;
import vn.iotstar.jobhub_hcmute_be.dto.SignUpMailDTO;
import vn.iotstar.jobhub_hcmute_be.entity.RefreshToken;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.exception.UserNotFoundException;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.RefreshTokenService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

        if (userService.findByEmail(loginDTO.getUserLogin()).isEmpty())
            throw new UserNotFoundException("Account does not exist");
        Optional<User> optionalUser = userService.findByEmail(loginDTO.getUserLogin());
        if (optionalUser.isPresent() && !optionalUser.get().isVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.builder()
                    .success(false)
                    .message("Your account is not verified!")
                    .result(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build());
        }

        return userService.userLogin(loginDTO);

    }

    @PostMapping("/signup-email")
    public ResponseEntity<GenericResponse> signUpMail(
            @RequestBody @Valid SignUpMailDTO signUpMailDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(
                    bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.status(500)
                    .body(new GenericResponse(
                            false,
                            errorMessage,
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
        return userService.userRegisterEmail(signUpMailDTO);
    }

}
