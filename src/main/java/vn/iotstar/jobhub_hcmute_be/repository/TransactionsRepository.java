package vn.iotstar.jobhub_hcmute_be.repository;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, String> {
    Optional<Transactions> findByEmployerAndStatus(Employer employer, String pending);

    Page<Transactions> findAllByEmployer(Employer employer, Pageable pageable);

    Long countByIdIsNotNull();

}