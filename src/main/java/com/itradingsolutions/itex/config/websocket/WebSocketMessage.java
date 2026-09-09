package com.itradingsolutions.itex.config.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mensaje emitido hacia los clientes WebSocket conectados.
 *
 * @param data                 contenido del mensaje (record inmutable o texto plano).
 * @param webSocketMessageValue tipo de evento emitido.
 * @param sessionId            identificador de la sesion WebSocket destinataria.
 * @param <T>                  tipo del contenido del mensaje.
 */
public record WebSocketMessage<T>(
        T data,
        WebSocketMessageValue webSocketMessageValue,
        String sessionId) {

    /**
     * Tipo de mensaje derivado del valor del evento, se serializa como
     * {@code webSocketMessageType} para mantener el contrato JSON existente.
     */
    @JsonProperty("webSocketMessageType")
    public WebSocketMessageType webSocketMessageType() {
        return webSocketMessageValue.getType();
    }
}
