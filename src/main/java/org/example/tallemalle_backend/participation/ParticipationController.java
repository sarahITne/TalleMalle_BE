package org.example.tallemalle_backend.participation;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.participation.model.Participation;
import org.example.tallemalle_backend.participation.model.ParticipationDto;
import org.example.tallemalle_backend.participation.model.ParticipationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruits")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationRepository participationRepository;

    @GetMapping("/{recruitId}/participants")
    public ResponseEntity participants(@PathVariable Long recruitId) {
        List<ParticipationDto.MemberRes> members = participationRepository.findAllByRecruit_IdxAndStatus(recruitId, ParticipationStatus.ACTIVE)
                .stream()
                .map(Participation::getUser)
                .map(ParticipationDto.MemberRes::from)
                .toList();

        return ResponseEntity.ok(BaseResponse.success(members));
    }
}
