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
            long userId = Long.parseLong(req.getParameter("idx"));

            NotificationDto.NotificationListRes returnDto = notificationService.read(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("summary") && req.getMethod().equals("GET")) {
            long userId = Long.parseLong(req.getParameter("idx"));

            NotificationDto.NotificationListRes returnDto = notificationService.readUnreadOnly(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("readall") && req.getMethod().equals("PATCH")) {
            long userId = Long.parseLong(req.getParameter("idx"));

            NotificationDto.NotificationReadAllRes returnDto = notificationService.readAll(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("readonly") && req.getMethod().equals("PATCH")) {
            long notificationId = Long.parseLong(req.getParameter("id"));
            long userId = Long.parseLong(req.getParameter("idx"));

            NotificationDto.NotificationReadRes returnDto = notificationService.readOne(notificationId, userId);

            return BaseResponse.success(returnDto);

        }
        return null;
    }
}
