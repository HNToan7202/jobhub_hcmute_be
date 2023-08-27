package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Optional;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailAndIsActiveIsTrue(String email);

    Optional<User> findByPhoneAndIsActiveIsTrue(String phone);

    Optional<User> findByUserIdAndIsActiveIsTrue(String userId);

    Optional<User> findByEmail(String email);

}