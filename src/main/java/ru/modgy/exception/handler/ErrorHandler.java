package ru.modgy.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.modgy.exception.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    public static final String INCORRECTLY_MADE_REQUEST =
            "Запрос составлен неверно";
    public static final String FOR_THE_REQUESTED_OPERATION_THE_CONDITIONS_ARE_NOT_MET =
            "Невыполнимые условия для запрошенной операции";

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMissingRequestHeaderException(final MissingRequestHeaderException ex) {
        log.error("EH: MissingRequestHeaderException: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "Required request header is not present.",
                HttpStatus.BAD_REQUEST,
                now()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Error handleAccessDeniedException(final AccessDeniedException ex) {
        log.error("EH: AccessDeniedException: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "Operation is denied for this user.",
                HttpStatus.FORBIDDEN,
                now()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error errorMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        Error error = new Error(
                new ArrayList<>(),
                Objects.requireNonNull(fieldError).getDefaultMessage(),
                "Ошибка запроса",
                HttpStatus.BAD_REQUEST,
                now());
        log.error("EH: MethodArgumentNotValidException: {}, ex : {}, error : {}", ex.getMessage(), ex, error);
        return error;
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error errorConstraintViolationExceptionJakarta(final jakarta.validation.ConstraintViolationException ex) {
        log.error("EH: errorConstraintViolationExceptionJakarta: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                INCORRECTLY_MADE_REQUEST,
                HttpStatus.BAD_REQUEST,
                now());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error errorMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        log.error("EH: MethodArgumentTypeMismatchException: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                String.format("%s. Param: %s Value=%s", ex.getMessage(), ex.getName(), ex.getValue()),
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST,
                now());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error errorMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        log.error("EH: MissingServletRequestParameterException: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST,
                now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error errorDataIntegrityViolationException(final DataIntegrityViolationException ex) {
        ConstraintViolationException cause = (ConstraintViolationException) ex.getCause();
        String message = ExceptionMessages.dictionary.get(cause.getConstraintName());
        Error error = new Error(
                new ArrayList<>(),
                message,
                "Нарушение целостности данных БД.",
                HttpStatus.CONFLICT,
                now());
        log.error("EH: DataIntegrityViolationException: {}, ex: {}, error: {}", ex.getMessage(), ex, error);
        return error;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error errorConstraintViolationException(final ConstraintViolationException ex) {
        log.error("EH: ConstraintViolationException: {}", ex.getMessage(), ex);
        return new Error(
                Collections.emptyList(),
                ex.getMessage(),
                "Integrity constraint has been violated.",
                HttpStatus.CONFLICT,
                now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFoundException(final NotFoundException ex) {
        log.error("EH: NotFoundException: {}", ex.getMessage(), ex);
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                now());
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleInvalidDateRangeException(final InvalidDateRangeException ex) {
        log.error("EH: InvalidDateRangeException: {}", ex.getMessage());
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                FOR_THE_REQUESTED_OPERATION_THE_CONDITIONS_ARE_NOT_MET,
                HttpStatus.BAD_REQUEST,
                now());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleHttpMessageConversionException(final HttpMessageConversionException ex) {
        log.error("EH: HttpMessageConversionException: {}", ex.getMessage());
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "Request parameters validation error.",
                HttpStatus.CONFLICT,
                now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleConflictException(final ConflictException ex) {
        log.error("EH: ConflictException: {}", ex.getMessage());
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT,
                now());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadRequestException(final BadRequestException ex) {
        log.error("EH: BadRequestException: {}", ex.getMessage());
        return new Error(
                new ArrayList<>(),
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST,
                now());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error errorThrowableException(final Throwable ex) {
        log.error("EH: Internal Server Error. {}", ex.getMessage(), ex);
        return new Error(
                Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList(),
                ex.getMessage(),
                "Internal Server Error.",
                HttpStatus.I_AM_A_TEAPOT,
                now());
    }

    record Error(List<String> errors, String message, String reason, HttpStatus httpStatus, LocalDateTime timeStamp) {
    }
}

