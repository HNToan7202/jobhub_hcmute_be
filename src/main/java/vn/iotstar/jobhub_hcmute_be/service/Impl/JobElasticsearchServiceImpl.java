//package vn.iotstar.jobhub_hcmute_be.service.Impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import vn.iotstar.jobhub_hcmute_be.entity.JobModel;
//import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
//import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
//import vn.iotstar.jobhub_hcmute_be.repository.ElasticsearchRepository.JobElasticsearchRepository;
//import vn.iotstar.jobhub_hcmute_be.service.JobElasticsearchService;
//
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//public class JobElasticsearchServiceImpl implements JobElasticsearchService {
//
//    @Autowired
//    JobElasticsearchRepository jobElasticsearchRepository;
//
//    @Override
//    public Page<JobModel> searchSimilar(JobModel entity, String[] fields, Pageable pageable) {
//        return jobElasticsearchRepository.searchSimilar(entity, fields, pageable);
//    }
//
//    @Override
//    public Iterable<JobModel> findAll(Sort sort) {
//        return jobElasticsearchRepository.findAll(sort);
//    }
//
//    @Override
//    public Page<JobModel> findAll(Pageable pageable) {
//        return jobElasticsearchRepository.findAll(pageable);
//    }
//
//    @Override
//    public <S extends JobModel> S save(S entity) {
//        return jobElasticsearchRepository.save(entity);
//    }
//
//    @Override
//    public <S extends JobModel> Iterable<S> saveAll(Iterable<S> entities) {
//        return jobElasticsearchRepository.saveAll(entities);
//    }
//
//    @Override
//    public Optional<JobModel> findById(String s) {
//        return jobElasticsearchRepository.findById(s);
//    }
//
//    @Override
//    public boolean existsById(String s) {
//        return jobElasticsearchRepository.existsById(s);
//    }
//
//    @Override
//    public Iterable<JobModel> findAll() {
//        return jobElasticsearchRepository.findAll();
//    }
//
//    @Override
//    public Iterable<JobModel> findAllById(Iterable<String> strings) {
//        return jobElasticsearchRepository.findAllById(strings);
//    }
//
//    @Override
//    public long count() {
//        return jobElasticsearchRepository.count();
//    }
//
//    @Override
//    public void deleteById(String s) {
//        jobElasticsearchRepository.deleteById(s);
//    }
//
//    @Override
//    public void delete(JobModel entity) {
//        jobElasticsearchRepository.delete(entity);
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends String> strings) {
//        jobElasticsearchRepository.deleteAllById(strings);
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends JobModel> entities) {
//        jobElasticsearchRepository.deleteAll(entities);
//    }
//
//    @Override
//    public void deleteAll() {
//        jobElasticsearchRepository.deleteAll();
//    }
//
//
//
//    @Override
//    public ActionResult searchJobs(String name, String location, String position, Pageable pageable) {
//        ActionResult actionResult = new ActionResult();
//        Page<JobModel> jobModels = jobElasticsearchRepository.findAllByNameContainingOrLocationContainingOrPosition(name, location, position, pageable);
//
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("content", jobModels.getContent());
//        map.put("pageNumber", jobModels.getNumber());
//        map.put("pageSize", jobModels.getSize());
//        map.put("totalPages", jobModels.getTotalPages());
//        map.put("totalElements", jobModels.getTotalElements());
//        actionResult.setData(map);
//        actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_BY_FILTER_SUCCESS);
//        return actionResult;
//    }
//
//    @Override
//    public ActionResult findAllJobs(Pageable pageable){
//        ActionResult actionResult = new ActionResult();
//        Page<JobModel> jobModels = findAll(pageable);
//
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("content", jobModels.getContent());
//        map.put("pageNumber", jobModels.getNumber());
//        map.put("pageSize", jobModels.getSize());
//        map.put("totalPages", jobModels.getTotalPages());
//        map.put("totalElements", jobModels.getTotalElements());
//        actionResult.setData(map);
//        actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_JOB_SUCCESS);
//        return actionResult;
//    }
//
//}
