package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(MultipartFile imageFile) throws IOException;

    void deleteImage(String imageUrl) throws IOException;
}
