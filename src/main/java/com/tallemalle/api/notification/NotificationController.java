package com.tallemalle.api.notification;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.notification.model.NotificationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public class NotificationController implements Controller {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {

        if (req.getRequestURI().contains("list") && req.getMethod().equals("GET")) {
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);
            NotificationDto.NotificationListRes data = this.notificationService.read(userId);
            return BaseResponse.success(data);
        } else if (req.getRequestURI().contains("summary") && req.getMethod().equals("GET")) {
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);
            NotificationDto.NotificationListRes data = this.notificationService.readUnreadOnly(userId);

            return BaseResponse.success(data);
        } else if (req.getRequestURI().contains("readall") && req.getMethod().equals("PATCH")) {
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);

            Map<String, Object> data = this.notificationService.readAll(userId);

            return BaseResponse.success(data);
        } else if (req.getRequestURI().contains("readonly") && req.getMethod().equals("PATCH")) {
            // 1. 파라미터 2개 받기
            String notiIdStr = req.getParameter("id");
            String userIdStr = req.getParameter("idx");

            long notificationId = Long.parseLong(notiIdStr);
            long userId = Long.parseLong(userIdStr);

            boolean success = this.notificationService.readOne(notificationId, userId);

            return BaseResponse.success("알림을 읽음 처리했습니다.");

        }

        return null;
    }
}
