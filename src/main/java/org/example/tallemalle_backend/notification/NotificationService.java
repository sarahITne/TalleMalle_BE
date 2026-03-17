package org.example.tallemalle_backend.notification;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.notification.model.Notification;
import org.example.tallemalle_backend.notification.model.NotificationDto;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationDto.PageRes list(Long idx, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("idx").descending());

        Page<Notification> result = notificationRepository.findAllByUserIdx(idx, pageRequest);

        return NotificationDto.PageRes.from(result);
    }

    public List<NotificationDto.ReadTop5Res> summary(AuthUserDetails user) {
        List<Notification> entitiesTop5 = notificationRepository.findTop5ByUserIdxOrderByCreatedAtDesc(user.getIdx());
        return entitiesTop5.stream().map(NotificationDto.ReadTop5Res::from).toList();
    }

    public NotificationDto.ReadOnlyRes readOnly(AuthUserDetails user, Long idx) {
        Notification entity = notificationRepository.findByUserIdxAndIdx(user.getIdx(), idx).orElseThrow();
        entity.setRead(true);
        Notification result = notificationRepository.save(entity);
        return NotificationDto.ReadOnlyRes.from(result);
    }

    public void readAll(AuthUserDetails user) {
        List<Notification> entities = notificationRepository.findAllByUserIdx(user.getIdx());
        for(Notification entity : entities){
            entity.setRead(true);
            notificationRepository.save(entity);
        }
    }
}
