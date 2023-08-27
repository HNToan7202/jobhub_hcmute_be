package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.entity.RefreshToken;

public interface RefreshTokenService {
    <S extends RefreshToken> S save(S entity);

    void revokeRefreshToken(String userId);
}
