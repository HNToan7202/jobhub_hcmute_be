package vn.iotstar.jobhub_hcmute_be.repository.ElasticsearchRepository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.JobModel;

@Repository
@Hidden
public interface JobElasticsearchRepository extends ElasticsearchRepository<JobModel, String> {
    Page<JobModel> findAllByNameContainingOrLocationContainingOrPosition(String name, String location, String position, Pageable pageable);
}
