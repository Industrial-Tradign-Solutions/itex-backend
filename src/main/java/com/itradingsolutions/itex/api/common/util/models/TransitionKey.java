package com.itradingsolutions.itex.api.common.util.models;

public record TransitionKey<S extends Enum<S>>(S from, S to) {
}
