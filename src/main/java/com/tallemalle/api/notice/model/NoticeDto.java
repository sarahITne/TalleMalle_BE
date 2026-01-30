//package com.tallemalle.api.notice.model;
//
//import java.time.LocalDateTime;
//
//public class NoticeDto {
////    private Long noticeIdx;             // 공지사항 idx (PK)
////    private String title;               // 제목
////    private String contents;            // 내용
////    private String tag;                 // 태그
////    private Boolean isPinned;           // 필독 여부
////    private Integer views;              // 조회수
////    private LocalDateTime createdAt;    // 생성일시
////    private LocalDateTime updatedAt;    // 수정일시
//
//    // 기본 생성자
//    private NoticeDto() {
//
//
////    // 기본 생성자 2
////    public NoticeDto(Long noticeIdx, String title, String contents, String tag, Boolean isPinned, Integer views, LocalDateTime createdAt, LocalDateTime updatedAt) {
////        this.noticeIdx = noticeIdx;
////        this.title = title;
////        this.contents = contents;
////        this.tag = tag;
////        this.isPinned = isPinned;
////        this.views = views;
////        this.createdAt = createdAt;
////        this.updatedAt = updatedAt;
////    }
//
//        // 공지사항 리스트 조회 - 응답
//        public static class NoticeRes {
//            private Long noticeIdx;             // 공지사항 idx (PK)
//            private String title;               // 제목
//            private String tag;                 // 태그
//            private Boolean isPinned;           // 필독 여부
//            private LocalDateTime createdAt;    // 생성일시
//
//
//            public NoticeRes(Long noticeIdx, String title, String tag, Boolean isPinned, LocalDateTime createdAt) {
//                this.noticeIdx = noticeIdx;
//                this.title = title;
//                this.tag = tag;
//                this.isPinned = isPinned;
//                this.createdAt = createdAt;
//            }
//
//            public Long getNoticeIdx() {
//                return noticeIdx;
//            }
//
//            public String getTitle() {
//                return title;
//            }
//
//            public String getTag() {
//                return tag;
//            }
//
//            public Boolean getPinned() {
//                return isPinned;
//            }
//
//            public LocalDateTime getCreatedAt() {
//                return createdAt;
//        }
//    }
//
//    // 공지사항 상세 조회 - 요청
//
//
//    // 공지사항 상세 조회 - 응답
//
//}
//}
