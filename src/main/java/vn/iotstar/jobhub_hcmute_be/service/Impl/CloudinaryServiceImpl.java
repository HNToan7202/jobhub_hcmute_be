package vn.iotstar.jobhub_hcmute_be.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("File is null. Please upload a valid file.");
        }
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
        Map<String, String> params= ObjectUtils.asMap(
                "folder", "Recruiment Assets/User",
                "resource_type", "image");
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
//        return cloudinary.url().format().generate(uploadResult.get("public_id").toString());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        Map<String, String> params= ObjectUtils.asMap(
                "folder", "Recruiment Assets/User",
                "resource_type", "image");
        Map result = cloudinary.uploader().destroy(getPublicIdImage(imageUrl), params);
        System.out.println(result.get("result").toString());

    }

    public String getPublicIdImage(String imageUrl)  {
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
        String publicId = "Recruiment Assets/User/" + imageName;
        return publicId;
    }

    @Override
    public String uploadResume(MultipartFile imageFile, String userId) throws IOException {

        String id = UUID.randomUUID().toString().split("-")[0];
        String nameCV= "cv_"+userId+"_"+id;
        Map<String, String> params= ObjectUtils.asMap(
                "folder", "Recruiment Assets/CV",
                "public_id",imageFile.getOriginalFilename(),
                "resource_type", "auto",
                "format","pdf");
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
//        return cloudinary.url().format().generate(uploadResult.get("public_id").toString());
        System.out.println(uploadResult.get("public_id").toString());
        return (String) uploadResult.get("secure_url");
    }
}
