package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface JobApplyService {
    <S extends JobApply> List<S> saveAll(Iterable<S> entities);

    List<JobApply> findAll();

    List<JobApply> findAllById(Iterable<String> strings);

    <S extends JobApply> S save(S entity);

    Optional<JobApply> findById(String s);

    boolean existsById(String s);

    long count();

    void deleteById(String s);

    void delete(JobApply entity);

    void deleteAllById(Iterable<? extends String> strings);

    void deleteAll(Iterable<? extends JobApply> entities);

    void deleteAll();

    List<JobApply> findAll(Sort sort);

    Page<JobApply> findAll(Pageable pageable);

    <S extends JobApply> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends JobApply> long count(Example<S> example);

    <S extends JobApply, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
