package com.tallemalle.api.notification;

import com.tallemalle.api.auth.context.LoginUser;
import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.notification.model.NotificationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NotificationController implements Controller {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {

        if (req.getRequestURI().contains("list") && req.getMethod().equals("GET")) {
            LoginUser loginUser = (LoginUser) req.getAttribute("LoginUser");
            Long userId = loginUser.getUserId();

            NotificationDto.NotificationListRes returnDto = notificationService.read(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("summary") && req.getMethod().equals("GET")) {
            LoginUser loginUser = (LoginUser) req.getAttribute("LoginUser");
            Long userId = loginUser.getUserId();

            NotificationDto.NotificationListRes returnDto = notificationService.readUnreadOnly(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("readall") && req.getMethod().equals("PATCH")) {
            LoginUser loginUser = (LoginUser) req.getAttribute("LoginUser");
            Long userId = loginUser.getUserId();

            NotificationDto.NotificationReadAllRes returnDto = notificationService.readAll(userId);

            return BaseResponse.success(returnDto);

        } else if (req.getRequestURI().contains("readonly") && req.getMethod().equals("PATCH")) {
            Long notificationId = Long.parseLong(req.getParameter("id"));
            LoginUser loginUser = (LoginUser) req.getAttribute("LoginUser");
            Long userId = loginUser.getUserId();

            NotificationDto.NotificationReadRes returnDto = notificationService.readOne(notificationId, userId);

            return BaseResponse.success(returnDto);

        }
        return null;
    }
}
