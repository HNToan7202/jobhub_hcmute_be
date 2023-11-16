package vn.iotstar.jobhub_hcmute_be.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@Configuration
//file này để cấu hình firebase
public class FirebaseConfig {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        //đọc file json để lấy thông tin xác thực
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource("job-hub-3c313-firebase-adminsdk-uq6ky-4e829ecda4.json").getInputStream());
        //tạo option để cấu hình
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                //set thông tin xác thực
                .setCredentials(googleCredentials)
                //build
                .build();
        //khởi tạo app để cấu hình
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        if (firebaseApps != null && !firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals("my-app")) {
                    // FirebaseApp đã tồn tại, không cần khởi tạo lại
                    return FirebaseMessaging.getInstance(app);
                }
            }
        }
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
        return FirebaseMessaging.getInstance(app);//trả về instance của firebase
    }
}