package vn.iotstar.jobhub_hcmute_be.config;


//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    @Bean(name = "asyncTaskExecutor")
//    public ThreadPoolTaskExecutor asyncTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // Số lượng luồng cố định
//        executor.setMaxPoolSize(10); // Số lượng luồng tối đa
//        executor.setQueueCapacity(25); // Số lượng công việc đang đợi trong hàng đợi
//        executor.setThreadNamePrefix("AsyncThread-"); // Tiền tố tên của các luồng
//        executor.initialize(); // Khởi tạo executor
//        return executor;
//    }
//}