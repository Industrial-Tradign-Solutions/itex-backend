package com.itradingsolutions.itex.config.websocket;

import lombok.Getter;

@Getter
public enum WebSocketMessageValue {
    LIST_ROLES(WebSocketMessageType.LIST),
    LIST_INDUSTRIES(WebSocketMessageType.LIST),
    LIST_DEPARTMENTS(WebSocketMessageType.LIST),
    LIST_COUNTRIES(WebSocketMessageType.LIST),
    LIST_STATES(WebSocketMessageType.LIST),
    LIST_CITIES(WebSocketMessageType.LIST),
    LIST_USERS(WebSocketMessageType.LIST),

    NEW_LOGIN(WebSocketMessageType.LOGOUT),
    NOTIFICATION_LOGOUT(WebSocketMessageType.LOGOUT),
    CLOSE_ALL_SESSIONS(WebSocketMessageType.LOGOUT),
    DISABLE_USER(WebSocketMessageType.LOGOUT),
    DISABLE_ROLE(WebSocketMessageType.LOGOUT),


    OPEN_PROSPECTS(WebSocketMessageType.OPEN_RECORDS),
    ERROR_SOCKET(WebSocketMessageType.ERROR);


    private final WebSocketMessageType type;

    WebSocketMessageValue(WebSocketMessageType type) {
        this.type = type;
    }
}
