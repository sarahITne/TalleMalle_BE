package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.chat.model.Chat;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.upload.UploadService;
import org.example.tallemalle_backend.upload.PresignedUploadDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send/{recruitIdx}")
    public void sendMessage(
            @DestinationVariable Long recruitIdx,
            ChatDto.SendReq dto,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        AuthUserDetails user = resolveUser(headerAccessor);
        ChatDto.SendRes result = chatService.send(user, recruitIdx, dto);
        messagingTemplate.convertAndSend("/topic/chat/" + recruitIdx, result);
    }

    @GetMapping("/{recruitIdx}/messages")
    public ResponseEntity list(
            @AuthenticationPrincipal AuthUserDetails user,
            @PathVariable Long recruitIdx,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        List<ChatDto.ListRes> dto = chatService.list(user, recruitIdx, before, size);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/unread")
    public ResponseEntity unread(
            @AuthenticationPrincipal AuthUserDetails user
    ) {
        List<Long> dto = chatService.unreadRecruitIds(user);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @GetMapping("/rooms")
    public ResponseEntity rooms(
            @AuthenticationPrincipal AuthUserDetails user
    ) {
        List<ChatDto.RoomRes> dto = chatService.rooms(user);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    // 이미지 전송
    private final UploadService uploadService;

    @PostMapping("/image/presign")
    public ResponseEntity presign(@RequestBody PresignedUploadDto.PresignReq req) {
        PresignedUploadDto.PresignRes result = uploadService.presign(req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    private AuthUserDetails resolveUser(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if (attributes == null) {
            throw new IllegalStateException("웹소켓 세션이 없습니다.");
        }

        Object authObject = attributes.get("user");
        if (!(authObject instanceof Authentication authentication)) {
            throw new IllegalStateException("웹소켓 인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthUserDetails user)) {
            throw new IllegalStateException("웹소켓 사용자 정보가 없습니다.");
        }

        return user;
    }
}
