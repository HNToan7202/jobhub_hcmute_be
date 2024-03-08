package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Role;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailAndIsActiveIsTrue(String email);


    Optional<User> findByPhoneAndIsActiveIsTrue(String phone);

    Optional<User> findByUserIdAndIsActiveIsTrue(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    Page<User> findByRole_NameAndIsActiveIsTrue(String roleId, Pageable pageable);

    Page<User> findByRole_NameAndIsActive(String role, boolean isActive, Pageable pageable);

    Page<User> findByIsActive(boolean isActive, Pageable pageable);

    Page<User> findByRole_Name(String roleName, Pageable pageable);

    Long countByUserIdIsNotNull();

    //Lấy tổng user role khác admin
    Long countByRole_NameNot(String roleName);

    List<User> findByUserIdIn(List<String> friendIds);
}