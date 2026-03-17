package org.example.tallemalle_backend.push.model;

import lombok.Getter;

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
}
