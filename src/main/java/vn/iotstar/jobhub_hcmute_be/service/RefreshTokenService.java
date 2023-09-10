package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.http.ResponseEntity;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.RefreshToken;

public interface RefreshTokenService {
    <S extends RefreshToken> S save(S entity);

    ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken);

    void revokeRefreshToken(String userId);

    ResponseEntity<?> logout(String refreshToken);
}
