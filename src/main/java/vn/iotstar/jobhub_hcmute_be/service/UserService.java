package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Sort;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    @Deprecated
    User getById(String s);

    List<User> findAll();

    <S extends User> S save(S entity);

    Optional<User> findById(String s);

    long count();

    void deleteById(String s);

    void delete(User entity);

    List<User> findAll(Sort sort);
}
