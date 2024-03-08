package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Photo;
import vn.iotstar.jobhub_hcmute_be.entity.Post;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    Page<Photo> findByPost(Post post, Pageable pageable);
}