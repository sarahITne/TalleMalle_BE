package org.example.tallemalle_backend.profile;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.profile.data.dto.ProfileDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/read")
    public ResponseEntity read(@AuthenticationPrincipal AuthUserDetails user) {
        return ResponseEntity.ok(BaseResponse.success(profileService.read(user)));
    }

    @PostMapping("/update")
    public ResponseEntity update(@AuthenticationPrincipal AuthUserDetails user,
                                 @RequestBody ProfileDto.UpdateRequest dto) {
        return ResponseEntity.ok(BaseResponse.success(profileService.update(user, dto)));
    }
}
