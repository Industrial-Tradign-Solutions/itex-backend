package com.itradingsolutions.itex.config.websocket;

import lombok.Getter;

@Getter
public enum WebSocketMessageValue {
    NEW_LOGIN(WebSocketMessageType.LOGOUT),
    NOTIFICATION_LOGOUT(WebSocketMessageType.LOGOUT),
    CLOSE_ALL_SESSIONS(WebSocketMessageType.LOGOUT),
    DISABLE_USER(WebSocketMessageType.LOGOUT),
    DISABLE_ROLE(WebSocketMessageType.LOGOUT),

    ERROR_SOCKET(WebSocketMessageType.ERROR);

    private final WebSocketMessageType type;

    WebSocketMessageValue(WebSocketMessageType type) {
        this.type = type;
    }
}
