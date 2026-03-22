package org.example.tallemalle_backend.profile;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.profile.data.dto.ProfileDto;
import org.example.tallemalle_backend.upload.PresignedUploadDto;
import org.example.tallemalle_backend.upload.UploadService;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UploadService uploadService;

    @GetMapping("/profile")
    public ResponseEntity read(@AuthenticationPrincipal AuthUserDetails user) {
        return ResponseEntity.ok(BaseResponse.success(profileService.read(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity update(@AuthenticationPrincipal AuthUserDetails user,
                                 @RequestBody ProfileDto.UpdateRequest dto) {
        return ResponseEntity.ok(BaseResponse.success(profileService.update(user, dto)));
    }

    @PostMapping("/image/presign")
    public ResponseEntity presign(@RequestBody PresignedUploadDto.PresignReq req) {
        PresignedUploadDto.PresignRes result = uploadService.presign(req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
