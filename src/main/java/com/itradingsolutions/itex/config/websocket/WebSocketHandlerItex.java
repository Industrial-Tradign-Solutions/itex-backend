package com.itradingsolutions.itex.config.websocket;

import com.itradingsolutions.itex.config.security.jwt.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandlerItex extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = new HashSet<>();
    private final JWTService jwtService;
    // Jackson 3 trae soporte nativo de java.time y WRITE_DATES_AS_TIMESTAMPS
    // deshabilitado por defecto, asi que el JSON emitido es identico al de
    // Jackson 2 con JavaTimeModule + disable(WRITE_DATES_AS_TIMESTAMPS).
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketMessage<String> data = new WebSocketMessage<>("Error al ingresar al socket", WebSocketMessageValue.ERROR_SOCKET);
        data.setSessionId(session.getId());
        String jsonData = mapper.writeValueAsString(data);
        TextMessage message = new TextMessage(jsonData);
        String token = Objects.requireNonNull(session.getUri()).getQuery().replace("token=", "");
        if (token.isEmpty()) {
            sendOneMessage(session, message);
        } else  {
            if (jwtService.validateToken(JWTService.TOKEN_PREFIX + token)) {
                sessions.add(session);
            } else {
                sendOneMessage(session, message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        session.close(status);
    }

    public <T> void sendMessage(WebSocketMessage<T> data) {
        try {
            for (WebSocketSession session: sessions) {
                data.setSessionId(session.getId());
                String jsonData = mapper.writeValueAsString(data);
                TextMessage message = new TextMessage(jsonData);
                sendOneMessage(session, message);
            }
        } catch (JacksonException ex) {
          log.error("Error when mapping the object", ex);
        }
    }

    public void closeSessionUser(String token, UUID userId, WebSocketMessageValue value) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", userId);
        sendMessage(new WebSocketMessage<>(data, value));
    }

    private void sendOneMessage(WebSocketSession session, TextMessage message) {
        try {
            session.sendMessage(message);
        } catch (IOException ex) {
            log.error("Could not send message via webSocket", ex);
        }
    }
}
