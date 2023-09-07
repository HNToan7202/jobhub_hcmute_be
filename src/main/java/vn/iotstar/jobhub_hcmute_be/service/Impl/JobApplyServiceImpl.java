package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.repository.JobApplyRepository;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JobApplyServiceImpl implements JobApplyService {

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Override
    public <S extends JobApply> List<S> saveAll(Iterable<S> entities) {
        return jobApplyRepository.saveAll(entities);
    }

    @Override
    public List<JobApply> findAll() {
        return jobApplyRepository.findAll();
    }

    @Override
    public List<JobApply> findAllById(Iterable<String> strings) {
        return jobApplyRepository.findAllById(strings);
    }

    @Override
    public <S extends JobApply> S save(S entity) {
        return jobApplyRepository.save(entity);
    }

    @Override
    public Optional<JobApply> findById(String s) {
        return jobApplyRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return jobApplyRepository.existsById(s);
    }

    @Override
    public long count() {
        return jobApplyRepository.count();
    }

    @Override
    public void deleteById(String s) {
        jobApplyRepository.deleteById(s);
    }

    @Override
    public void delete(JobApply entity) {
        jobApplyRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        jobApplyRepository.deleteAllById(strings);
    }

    @Override
    public void deleteAll(Iterable<? extends JobApply> entities) {
        jobApplyRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        jobApplyRepository.deleteAll();
    }

    @Override
    public List<JobApply> findAll(Sort sort) {
        return jobApplyRepository.findAll(sort);
    }

    @Override
    public Page<JobApply> findAll(Pageable pageable) {
        return jobApplyRepository.findAll(pageable);
    }

    @Override
    public <S extends JobApply> Page<S> findAll(Example<S> example, Pageable pageable) {
        return jobApplyRepository.findAll(example, pageable);
    }

    @Override
    public <S extends JobApply> long count(Example<S> example) {
        return jobApplyRepository.count(example);
    }

    @Override
    public <S extends JobApply, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return jobApplyRepository.findBy(example, queryFunction);
    }
}
