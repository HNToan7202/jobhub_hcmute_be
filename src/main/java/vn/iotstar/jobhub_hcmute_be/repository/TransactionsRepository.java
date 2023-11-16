package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;

import java.util.Optional;

@Hidden
@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, String> {
    Optional<Transactions> findByEmployerAndStatus(Employer employer, String pending);
}