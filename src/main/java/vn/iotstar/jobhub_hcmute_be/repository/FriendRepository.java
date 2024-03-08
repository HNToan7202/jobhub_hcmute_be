package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.constant.FriendStatus;
import vn.iotstar.jobhub_hcmute_be.entity.Friend;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    Optional<Friend> findBySenderAndReceiver(User user, User user1);


    Page<Friend> findBySenderOrReceiverAndStatus(User sender, User receiver, FriendStatus status, Pageable pageable);

    Page<Friend> findBySenderAndStatus(User user, FriendStatus friendStatus, Pageable pageable);
    Page<Friend> findByReceiverAndStatus(User user, FriendStatus friendStatus, Pageable pageable);
}