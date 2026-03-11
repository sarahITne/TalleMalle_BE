package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send/{recruitIdx}")
    public ResponseEntity send(
            @AuthenticationPrincipal AuthUserDetails user,
            @PathVariable Long recruitIdx,
            @RequestBody ChatDto.SendReq dto
    ) {
        ChatDto.SendRes result = chatService.send(user, recruitIdx, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{recruitIdx}/messages")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails user,
            @PathVariable Long recruitIdx
    ) {
        List<ChatDto.ListRes> dto = chatService.list(user, recruitIdx);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
}
