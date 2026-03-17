package org.example.tallemalle_backend.upload;

public interface UploadService {
    PresignedUploadDto.PresignRes presign(PresignedUploadDto.PresignReq req);
}
