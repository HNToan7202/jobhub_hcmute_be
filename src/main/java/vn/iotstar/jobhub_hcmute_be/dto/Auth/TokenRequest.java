package vn.iotstar.jobhub_hcmute_be.dto.Auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequest {
    private String refreshToken;
}
