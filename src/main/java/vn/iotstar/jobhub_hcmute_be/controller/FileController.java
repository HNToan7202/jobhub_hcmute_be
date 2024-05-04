package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.service.Impl.DownFileImpl;
import vn.iotstar.jobhub_hcmute_be.service.Impl.FileServiceImpl;

@RestController
@RequestMapping("/api/v1/file")
@Tag(name = "file", description = "File API")
public class FileController {
    @Autowired
    private DownFileImpl downFile;

    @GetMapping("/downloadNewUserList")
    public ResponseEntity<byte[]> downloadNewUserList(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "100") Integer size,
                                                      @RequestParam(defaultValue = "STUDENT") String roleStr,
                                                      @RequestParam(defaultValue = "2021-09-01") String startOfDayYesterday,
                                                      @RequestParam(defaultValue = "2021-09-01") String endOfDayYesterday) {
        {
            byte[] xlsxContent = downFile.downloadNewUserList(page, size, startOfDayYesterday, endOfDayYesterday, roleStr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = startOfDayYesterday + "_" + endOfDayYesterday + "_" + roleStr + ".xlsx";
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok().headers(headers).body(xlsxContent);
        }
    }
}
