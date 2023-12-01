package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import vn.iotstar.jobhub_hcmute_be.entity.BlackList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface BlackListService {
    void deleteAllInBatch(Iterable<BlackList> entities);

    <S extends BlackList> List<S> findAll(Example<S> example);

    <S extends BlackList> List<S> findAll(Example<S> example, Sort sort);

    <S extends BlackList> S save(S entity);

    void deleteById(String s);

    void delete(BlackList entity);

    List<BlackList> findAll(Sort sort);

    Page<BlackList> findAll(Pageable pageable);

    <S extends BlackList> Optional<S> findOne(Example<S> example);

    <S extends BlackList> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends BlackList> long count(Example<S> example);

    <S extends BlackList> boolean exists(Example<S> example);

    <S extends BlackList, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
