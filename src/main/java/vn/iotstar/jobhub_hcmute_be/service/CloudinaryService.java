package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(MultipartFile imageFile) throws IOException;

    String uploadVideo(MultipartFile imageFile) throws IOException;

    void deleteImage(String imageUrl) throws IOException;

    void deleteVideo(String videoUrl) throws IOException;

    String uploadFile(MultipartFile fileUrl) throws IOException;

    void deleteFile(String fileUrl) throws IOException;

    String uploadResume(MultipartFile imageFile, String userId) throws IOException;
}
