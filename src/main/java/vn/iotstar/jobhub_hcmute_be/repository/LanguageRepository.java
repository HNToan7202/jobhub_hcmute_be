package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, String> {
}