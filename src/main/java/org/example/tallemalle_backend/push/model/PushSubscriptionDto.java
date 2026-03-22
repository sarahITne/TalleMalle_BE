package org.example.tallemalle_backend.push.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PushSubscriptionDto {
    @Getter
    public static class SubscribeReq {
        private String endpoint;
        private Keys keys;

        @Getter
        public static class Keys {
            private String p256dh;
            private String auth;
        }
    }

    @Getter
    @Setter
    public static class PreferencesReq {
        private Boolean recruitPromotionPushEnabled;
    }

    @Getter
    @Builder
    public static class PreferencesRes {
        private boolean recruitPromotionPushEnabled;
    }
}
