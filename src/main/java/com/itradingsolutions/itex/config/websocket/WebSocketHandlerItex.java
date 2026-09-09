package com.itradingsolutions.itex.config.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itradingsolutions.itex.config.security.jwt.service.JWTService;
import com.itradingsolutions.itex.config.websocket.model.LogoutEventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler de WebSocket restringido a eventos de sesion de usuario:
 * notificaciones de nuevo login, cierre o inhabilitacion de sesiones.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandlerItex extends TextWebSocketHandler {

    private static final String TOKEN_QUERY_PARAM = "token";
    private static final String ERROR_SOCKET_MESSAGE = "Error al ingresar al socket";

    private final JWTService jwtService;
    private final ObjectMapper mapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        if (hasValidToken(session)) {
            sessions.add(session);
            log.info("WebSocket connection established. SessionId: {}", session.getId());
        } else {
            log.warn("WebSocket connection rejected: invalid or missing token. SessionId: {}", session.getId());
            sendErrorMessage(session);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket connection closed. SessionId: {}, status: {}", session.getId(), status);
    }

    /**
     * Notifica a todas las sesiones conectadas un evento de sesion de un usuario
     * (nuevo login, usuario o rol inhabilitado). El frontend filtra por token/userId.
     */
    public void sendLogoutEvent(String token, UUID userId, WebSocketMessageValue value) {
        broadcast(new LogoutEventData(token, userId), value);
    }

    /**
     * Envia una notificacion global de texto a todas las sesiones conectadas
     * (aviso de cierre del sistema, fin de sesion masiva).
     */
    public void sendSystemNotification(String message, WebSocketMessageValue value) {
        broadcast(message, value);
    }

    private <T> void broadcast(T data, WebSocketMessageValue value) {
        if (sessions.isEmpty()) {
            log.debug("No active WebSocket sessions to notify event '{}'", value);
            return;
        }
        log.info("Sending WebSocket event '{}' to {} active session(s)", value, sessions.size());
        sessions.forEach(session -> {
            try {
                WebSocketMessage<T> message = new WebSocketMessage<>(data, value, session.getId());
                String payload = mapper.writeValueAsString(message);
                sendOneMessage(session, new TextMessage(payload));
            } catch (JsonProcessingException ex) {
                log.error("Could not serialize WebSocket event '{}'. SessionId: {}", value, session.getId(), ex);
            }
        });
    }

    private boolean hasValidToken(WebSocketSession session) {
        String token = UriComponentsBuilder
                .fromUri(session.getUri())
                .build()
                .getQueryParams()
                .getFirst(TOKEN_QUERY_PARAM);
        return token != null && !token.isBlank() && jwtService.validateToken(JWTService.TOKEN_PREFIX + token);
    }

    private void sendErrorMessage(WebSocketSession session) {
        try {
            WebSocketMessage<String> message = new WebSocketMessage<>(ERROR_SOCKET_MESSAGE, WebSocketMessageValue.ERROR_SOCKET, session.getId());
            sendOneMessage(session, new TextMessage(mapper.writeValueAsString(message)));
        } catch (JsonProcessingException ex) {
            log.error("Could not serialize the WebSocket error message. SessionId: {}", session.getId(), ex);
        }
    }

    private void sendOneMessage(WebSocketSession session, TextMessage message) {
        try {
            synchronized (session) {
                session.sendMessage(message);
            }
        } catch (IOException ex) {
            log.error("Could not send message via WebSocket. SessionId: {}", session.getId(), ex);
        }
    }
}
