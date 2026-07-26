package com.itradingsolutions.itex.api.common.util.models;

import java.util.function.Consumer;
import java.util.function.Predicate;

public record StatusTransition<E>(Predicate<E> requirement, String requirementErrorKey, Consumer<E> sideEffect) {

    public static <E> StatusTransition<E> unrestricted(Consumer<E> sideEffect) {
        return new StatusTransition<>(entity -> true, null, sideEffect);
    }
}
