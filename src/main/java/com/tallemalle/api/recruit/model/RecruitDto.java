package com.tallemalle.api.recruit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecruitDto {
    private Long id; // PK (Primary Key)
    private String title; // 모집 글 제목
    private String desc; // 모집 글 상세 내용
    private Integer maxCount; // 최대 인원 (2~4)
    private Integer currentCount; // 현재 인원 (기본 1)
    private LocalDateTime departureTime; // 출발 시간
    private LocalDateTime createdAt; // 생성일
    private String startLocation; // 출발 위치
    private Double startLat; // 출발 위치 위도
    private Double startLng; // 출발 위치 경도
    private String endLocation; // 도착 위치
    private Double endLat; // 도착 위치 위도
    private Double endLng; // 도착 위치 경도
    private String status; // 방 상태 정보 "RECRUITING", "FULL", "CLOSED"
    private List<String> tags; // 태그 리스트
    private Long hostId; // 방장 정보

    public RecruitDto() {
    }

    public RecruitDto(Long id, String title, String desc, Integer maxCount, Integer currentCount, LocalDateTime departureTime, LocalDateTime createdAt, String startLocation, Double startLat, Double startLng, String endLocation, Double endLat, Double endLng, String status, List<String> tags, Long hostId) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.maxCount = maxCount;
        this.currentCount = currentCount;
        this.departureTime = departureTime;
        this.createdAt = createdAt;
        this.startLocation = startLocation;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLocation = endLocation;
        this.endLat = endLat;
        this.endLng = endLng;
        this.status = status;
        this.tags = tags;
        this.hostId = hostId;
    }

    public static class ListRes {
        public Long recruitId;
        public String title;
        public String startLocation;
        public String endLocation;
        public Integer currentCount;
        public Integer maxCount;
        public String departureTime;
        public String status;
        public Double startLat;
        public Double startLng;
        public List<String> tags;

        public ListRes(RecruitDto r) {
            this.recruitId = r.getId();
            this.title = r.getTitle();
            this.startLocation = r.getStartLocation();
            this.endLocation = r.getEndLocation();
            this.currentCount = r.getCurrentCount();
            this.maxCount = r.getMaxCount();
            this.status = r.getStatus();
            this.startLat = r.getStartLat();
            this.startLng = r.getStartLng();
            this.tags = r.getTags();

            // 날짜 포맷팅 (LocalDateTime -> String)
            if (r.getDepartureTime() != null) {
                this.departureTime = r.getDepartureTime()
                        .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createAt) {
        this.createdAt = createdAt;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public void setEndLng(Double endLng) {
        this.endLng = endLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}
