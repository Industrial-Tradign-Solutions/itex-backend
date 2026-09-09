package com.itradingsolutions.itex.config.websocket.model;

import java.util.UUID;

/**
 * Datos de un evento de sesion dirigido a un usuario (nuevo login o
 * inhabilitacion de usuario/rol). Sustituye al antiguo {@code Map} generico.
 *
 * @param token  token de la nueva sesion (nulo cuando el evento no proviene de un login).
 * @param userId identificador del usuario afectado por el evento.
 */
public record LogoutEventData(String token, UUID userId) {
}
