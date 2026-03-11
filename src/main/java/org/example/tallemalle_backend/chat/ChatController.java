package org.example.tallemalle_backend.chat;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.tallemalle_backend.chat.model.ChatDto;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chat")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity send(@RequestBody ChatDto.SendReq dto) {
        ChatDto.SendRes result = chatService.send(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity list() {
        List<ChatDto.ListRes> dto = chatService.list();
        return ResponseEntity.ok(BaseResponse.success(dto));
    }
}
