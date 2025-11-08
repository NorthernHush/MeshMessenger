package com.messengermesh.core.web;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final MinioClient minio;

    public FileController(@Value("${minio.endpoint:http://localhost:9000}") String endpoint,
                          @Value("${minio.accessKey:minioadmin}") String accessKey,
                          @Value("${minio.secretKey:minioadmin}") String secretKey) {
        this.minio = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    }

    @PostMapping("/request-upload")
    public ResponseEntity<?> requestUpload(@RequestBody Map<String,Object> req){
        // very small stub: return presigned PUT URL for a given filename
        String filename = (String) req.getOrDefault("filename","upload.bin");
        try{
            String url = minio.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket("uploads")
                    .object(filename)
                    .build());
            return ResponseEntity.ok(Map.of("uploadUrl", url, "fileId", filename));
        }catch(Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
