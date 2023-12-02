package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.iotstar.jobhub_hcmute_be.dto.ShortListDto;
import vn.iotstar.jobhub_hcmute_be.entity.ShortList;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.util.List;
import java.util.Optional;

public interface ShortListService {
    void deleteAllInBatch(Iterable<ShortList> entities);

    void deleteAllByIdInBatch(Iterable<String> strings);

    void deleteAllInBatch();

    ShortList getReferenceById(String s);

    <S extends ShortList> List<S> findAll(Example<S> example);

    <S extends ShortList> List<S> findAll(Example<S> example, Sort sort);

    List<ShortList> findAll();

    List<ShortList> findAllById(Iterable<String> strings);

    <S extends ShortList> S save(S entity);

    Optional<ShortList> findById(String s);

    boolean existsById(String s);

    long count();

    void deleteById(String s);

    void delete(ShortList entity);

    void deleteAllById(Iterable<? extends String> strings);

    void deleteAll(Iterable<? extends ShortList> entities);

    void deleteAll();

    List<ShortList> findAll(Sort sort);

    Page<ShortList> findAll(Pageable pageable);

    ActionResult addShortList(String userId, String jobId);

    ActionResult getShortListByUser(String userId, Pageable pageable);

    ActionResult getShortByJob(String jobId, Pageable pageable);

    ActionResult getShortListByEmployer(String userId, Pageable pageable);

    ActionResult deleteShortListById(String shortListId, String userId);
}
