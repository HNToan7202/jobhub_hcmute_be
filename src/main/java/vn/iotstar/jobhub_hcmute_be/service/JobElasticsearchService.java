//package vn.iotstar.jobhub_hcmute_be.service;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import vn.iotstar.jobhub_hcmute_be.entity.JobModel;
//import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
//
//import java.util.Optional;
//
//public interface JobElasticsearchService {
//    Page<JobModel> searchSimilar(JobModel entity, String[] fields, Pageable pageable);
//
//    Iterable<JobModel> findAll(Sort sort);
//
//    Page<JobModel> findAll(Pageable pageable);
//
//    <S extends JobModel> S save(S entity);
//
//    <S extends JobModel> Iterable<S> saveAll(Iterable<S> entities);
//
//    Optional<JobModel> findById(String s);
//
//    boolean existsById(String s);
//
//    Iterable<JobModel> findAll();
//
//    Iterable<JobModel> findAllById(Iterable<String> strings);
//
//    long count();
//
//    void deleteById(String s);
//
//    void delete(JobModel entity);
//
//    void deleteAllById(Iterable<? extends String> strings);
//
//    void deleteAll(Iterable<? extends JobModel> entities);
//
//    void deleteAll();
//
//   ActionResult searchJobs(String name, String location, String position, Pageable pageable);
//
//    ActionResult findAllJobs(Pageable pageable);
//}
