package com.itradingsolutions.itex.config.controller;

import com.itradingsolutions.itex.api.common.service.IMessageService;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.admin.role.exceptions.ActionNotAccessException;
import com.itradingsolutions.itex.api.admin.role.exceptions.ModuleNotAccessException;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.common.util.models.responses.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final IMessageService messageService;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handlerUncaughtException(Throwable t) {
        return buildErrorResponse(t, t.getMessage(), INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> invalidRequestErrorHandler(final MethodArgumentNotValidException e) {

        var errors = e.getBindingResult().getAllErrors().stream()
                        .filter(Objects::nonNull)
                        .map(this::getValidationErrorMessage)
                        .toList();

        Map<String, String> formErrors = new HashMap<>();

        e.getBindingResult().getAllErrors().stream()
                            .filter(Objects::nonNull)
                            .toList()
                .forEach(error -> {
                    if (error instanceof FieldError fe) {
                        formErrors.put(fe.getField(), error.getDefaultMessage());
                    }
                });

        return buildErrorResponse(e, String.join(" ", errors), BAD_REQUEST, formErrors);
    }

    public String getValidationErrorMessage(final ObjectError error) {
        final var errorMessage = new StringBuilder();
        if (error instanceof FieldError fe) {
            errorMessage.append("<").append(fe.getField()).append("> - ");
        }
        errorMessage.append(error.getDefaultMessage());
        return errorMessage.toString();
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlerObjectNotFoundException(final ObjectNotFoundException t) {
        return buildErrorResponse(t, t.getMessage(), NOT_FOUND, null);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlerNotFoundException(final NotFoundException t) {
        return buildErrorResponse(t, t.getMessage(), NOT_FOUND, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlerIllegalArgumentException(final IllegalArgumentException t) {
        return buildErrorResponse(t, t.getMessage(), BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException t) {
        String message = "Invalid value for parameter '" + t.getName() + "': " + t.getValue();
        return buildErrorResponse(t, message, BAD_REQUEST, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlerMissingParameter(final MissingServletRequestParameterException t) {
        String message = "Missing required parameter: " + t.getParameterName();
        return buildErrorResponse(t, message, BAD_REQUEST, null);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlerBadRequestException(final BadRequestException t) {
        return buildErrorResponse(t, t.getMessage(), BAD_REQUEST, null);
    }

    @ExceptionHandler(ModuleNotAccessException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<ErrorResponse> handlerModuleNotAccessException(final ModuleNotAccessException t) {
        return buildErrorResponse(t, t.getMessage(), FORBIDDEN, null);
    }

    @ExceptionHandler(ActionNotAccessException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<ErrorResponse> handlerActionNotAccessException(final ActionNotAccessException t) {
        return buildErrorResponse(t, t.getMessage(), FORBIDDEN, null);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(CONFLICT)
    public ResponseEntity<ErrorResponse> handlerDataIntegrityViolationException(final DataIntegrityViolationException t) {
        String message;
        try {
            message = messageFkUnique(t.getCause().getCause().getMessage());
        } catch (Exception ex) {
            message = messageService.compositeMessage("controller_advice.unexpected", new String[]{t.getCause().getCause().getMessage()});
        }

        return buildErrorResponse(t, message, CONFLICT, null);
    }

    private String messageFkUnique(String error) {
        var errors = error.split("\\(")[2].split("\\)");
        var val = errors[0];

        String template = UniqueDB.getListErrors().stream()
                .filter(error::contains)
                .findFirst().orElse(null);

        if (template == null) return messageService.compositeMessage("controller_advice.unexpected", new String[]{error});
        return messageService.compositeMessage(template, new String[]{val});
    }


    /**
     * Builds the {@code ErrorResponse} object to serve all error request and response generic message
     *
     * @param e          Exception thrown by the handler itself
     * @param message    Message to be shown in the consumer request
     * @param httpStatus HTTP status to be sent it to the consumer
     * @return ErrorRepose
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Throwable e, String message, HttpStatus httpStatus, Map<String, String> formErrors) {
        log.error(message, e);
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(message, httpStatus.value(), formErrors));
    }
}
