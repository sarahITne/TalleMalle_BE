package org.example.tallemalle_backend.participation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationDto;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Participation API", description = "모집글 참여자 조회 API")
@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationRepository participationRepository;

    @Operation(summary = "모집글 참여자 목록", description = "특정 모집글의 참여자 목록을 조회합니다.")
    @GetMapping("/{recruitId}/participants")
    public ResponseEntity participants(@PathVariable Long recruitId) {
        List<ParticipationDto.MemberRes> members = participationRepository
                .findAllByRecruitIdxAndStatusWithUserProfile(recruitId, ParticipationStatus.ACTIVE)
                .stream()
                .map(Participation::getUser)
                .map(ParticipationDto.MemberRes::from)
                .toList();

        return ResponseEntity.ok(BaseResponse.success(members));
    }
}
