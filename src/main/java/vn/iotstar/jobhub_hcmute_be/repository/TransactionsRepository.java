package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Transactions;

import java.util.Optional;

public interface TransactionsRepository extends JpaRepository<Transactions, String> {
    Optional<Transactions> findByEmployerAndStatus(Employer employer, String pending);
}