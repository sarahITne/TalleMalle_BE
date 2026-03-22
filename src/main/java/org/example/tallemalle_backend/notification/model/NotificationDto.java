package org.example.tallemalle_backend.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationDto {
    @Getter
    @Builder
    public static class PageRes{
        private List<ReadRes> boardList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static PageRes from(Page<Notification> result) {
            return PageRes.builder()
                    .boardList(result.get().map(NotificationDto.ReadRes::from).toList())
                    .totalPage(result.getTotalPages())
                    .totalCount(result.getTotalElements())
                    .currentPage(result.getPageable().getPageNumber())
                    .currentSize(result.getPageable().getPageSize())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReadRes{
        private Long idx;
        private String title;
        private String contents;
        private String type;
        @JsonProperty("isRead")
        private boolean isRead;
        private LocalDateTime createdAt;

        public static ReadRes from(Notification entity) {
            return ReadRes.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .type(entity.getType())
                    .isRead(entity.isRead())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReadTop5Res{
        private Long idx;
        private String title;
        @JsonProperty("isRead")
        private boolean isRead;
        private LocalDateTime created_at;

        public static ReadTop5Res from(Notification entity){
            return ReadTop5Res.builder()
                    .idx(entity.getIdx())
                    .title(entity.getTitle())
                    .created_at(entity.getCreatedAt())
                    .isRead(entity.isRead())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReadOnlyRes{
        private Long idx;
        @JsonProperty("isRead")
        private boolean isRead;

        public static ReadOnlyRes from(Notification entity){
            return ReadOnlyRes.builder()
                    .idx(entity.getIdx())
                    .isRead(entity.isRead())
                    .build();
        }
    }
}
