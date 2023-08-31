package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.RefreshToken;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    List<RefreshToken> findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);
    Optional<RefreshToken> findByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);

    Optional<RefreshToken> findByTokenAndExpiredIsFalseAndRevokedIsFalse(String refreshToken);

    Optional<RefreshToken> findByUserAndExpiredIsFalseAndRevokedIsFalse(User user);
}