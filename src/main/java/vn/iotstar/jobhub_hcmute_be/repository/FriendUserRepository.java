package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.FriendUser;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Optional;

@Hidden
public interface FriendUserRepository extends JpaRepository<FriendUser, String> {

    Optional<FriendUser> findByUserAndFriendId(User use, String friendId);



    Page<FriendUser> findByUser(User user, Pageable pageable);
}