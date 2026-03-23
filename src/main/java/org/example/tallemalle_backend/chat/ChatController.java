package org.example.tallemalle_backend.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Chat API", description = "채팅 메시지·채팅방·읽지 않은 메시지 API")
@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "채팅 메시지 전송(웹소켓)", description = "특정 모집글 채팅방으로 메시지를 전송하고 구독자에게 브로드캐스트합니다.")
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

    @Operation(summary = "채팅 메시지 목록 조회", description = "모집글 채팅방의 메시지를 페이지 단위로 조회합니다.")
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

    @Operation(summary = "읽지 않은 채팅방 목록", description = "읽지 않은 메시지가 있는 모집글 식별자 목록을 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity unread(
            @AuthenticationPrincipal AuthUserDetails user
    ) {
        List<Long> dto = chatService.unreadRecruitIds(user);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @Operation(summary = "내 채팅방 목록", description = "로그인 사용자가 참여 중인 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity rooms(
            @AuthenticationPrincipal AuthUserDetails user
    ) {
        List<ChatDto.RoomRes> dto = chatService.rooms(user);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    // 이미지 전송
    private final UploadService uploadService;

    @Operation(summary = "채팅 이미지 업로드 presign", description = "채팅 이미지 업로드를 위한 presigned URL을 발급합니다.")
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
