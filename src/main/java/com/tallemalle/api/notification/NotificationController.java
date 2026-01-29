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

        if (req.getRequestURI().contains("list") && req.getMethod().equals("GET")){
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);
            NotificationDto.Response data = this.notificationService.read(userId);

            return BaseResponse.success(data);
        } else if (req.getRequestURI().contains("summary") && req.getMethod().equals("GET")) {
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);
            NotificationDto.Response data = this.notificationService.readUnreadOnly(userId);

            return BaseResponse.success(data);
        } else if (req.getRequestURI().contains("readall") && req.getMethod().equals("POST")) {
            String idxStr = req.getParameter("idx");
            long userId = Long.parseLong(idxStr);

            Map<String, Object> data = this.notificationService.readAll(userId);

            return BaseResponse.success(data);
        }
        return null;
    }
}
