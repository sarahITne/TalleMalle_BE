package org.example.tallemalle_backend.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.profile.data.dto.ProfileDto;
import org.example.tallemalle_backend.recruit.model.RecruitDto;
import org.example.tallemalle_backend.upload.PresignedUploadDto;
import org.example.tallemalle_backend.upload.UploadService;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile API", description = "프로필 API")
@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UploadService uploadService;

    @Operation(summary = "프로필 조회", description = "사용자의 프로필을 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity read(@AuthenticationPrincipal AuthUserDetails user) {
        return ResponseEntity.ok(BaseResponse.success(profileService.read(user)));
    }

    @Operation(summary = "프로필 수정", description = "사용자의 프로필을 수정합니다.")
    @PutMapping("/profile")
    public ResponseEntity update(@AuthenticationPrincipal AuthUserDetails user,
                                 @RequestBody ProfileDto.UpdateRequest dto) {
        return ResponseEntity.ok(BaseResponse.success(profileService.update(user, dto)));
    }

    @Operation(summary = "탑승 기록 조회", description = "사용자의 탑승 기록을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity history(@AuthenticationPrincipal AuthUserDetails user) {
        List<RecruitDto.BoardingRecordRes> result = profileService.history(user);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "Presigned Url 발급", description = "프로필 사진 업로드 용 Presigned Url을 발급 받습니다.")
    @PostMapping("/image/presign")
    public ResponseEntity presign(@RequestBody PresignedUploadDto.PresignReq req) {
        PresignedUploadDto.PresignRes result = uploadService.presign(req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
