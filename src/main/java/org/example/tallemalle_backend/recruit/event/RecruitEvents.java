package org.example.tallemalle_backend.recruit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tallemalle_backend.recruit.model.RecruitDto;

public class RecruitEvents {

    @Getter
    @AllArgsConstructor
    public static class CreatedEvent {
        private RecruitDto.ListRes dto;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatedEvent {
        private RecruitDto.ListRes dto;
    }

    @Getter
    @AllArgsConstructor
    public static class DeletedEvent {
        private Long recruitIdx;
    }

    @Getter
    @AllArgsConstructor
    public static class FullEvent {
        private Long recruitIdx;
        private String startPointName;
        private String destPointName;
    }

    @Getter
    @AllArgsConstructor
    public static class UserLeftEvent {
        private Long recruitIdx;
        private Long userIdx;
        private String userName;
    }
}
