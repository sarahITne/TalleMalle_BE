package org.example.tallemalle_backend.upload;

import lombok.Builder;
import lombok.Getter;

public class PresignedUploadDto {
    @Getter
    public static class PresignReq {
        private String fileName;
        private String contentType;
    }

    @Builder
    @Getter
    public static class PresignRes {
        private String uploadUrl;
        private String publicUrl;
        private String filePath;
    }
}
