package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.ResumeDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Resume;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    <S extends Resume> List<S> saveAll(Iterable<S> entities);

    List<Resume> findAll();

    <S extends Resume> S save(S entity);

    void deleteById(String s);

    void delete(Resume entity);

    void deleteAll();

    List<Resume> findAll(Sort sort);

    Page<Resume> findAll(Pageable pageable);

    ResponseEntity<?> uploadResumeFile(MultipartFile resumFile, String userId) throws IOException;

    ResponseEntity<?> getResumeUpload(String userId);

    ResponseEntity<?> updateResume(ResumeDTO resumeDTO, String studentId);

    ResponseEntity<?> getDetailResume(String studentId);

    ResponseEntity<?> deleteResume(String resumeId, String userId) throws IOException;

    //ResponseEntity<?> deleteResume(String resumeId, String userId) throws IOException;
}
