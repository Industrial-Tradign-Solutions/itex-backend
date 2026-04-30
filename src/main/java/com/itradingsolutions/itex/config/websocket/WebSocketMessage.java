package com.itradingsolutions.itex.config.websocket;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class WebSocketMessage<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -5942088148865685998L;

    private T data;
    private WebSocketMessageValue webSocketMessageValue;
    private WebSocketMessageType webSocketMessageType;
    private String sessionId;

    public WebSocketMessage(T data, WebSocketMessageValue webSocketMessageValue) {
        this.data = data;
        this.webSocketMessageValue = webSocketMessageValue;
        this.webSocketMessageType = webSocketMessageValue.getType();
    }

}
