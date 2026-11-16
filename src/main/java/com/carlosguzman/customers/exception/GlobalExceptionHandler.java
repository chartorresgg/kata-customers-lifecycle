package com.carlosguzman.customers.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones. Convierte las excepciones lanzadas por los
 * controladores en respuestas HTTP con formato ProblemDetail (RFC 9457).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Responde 409 Conflict cuando se intenta registrar un cliente con un correo
     * que ya existe.
     *
     * @param ex excepción lanzada por el servicio.
     * @return el detalle del error con estado 409.
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicate(DuplicateEmailException ex) {
        log.warn("Intento de registro duplicado");
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Cliente duplicado");
        return problem;
    }

    /**
     * Responde 400 Bad Request cuando los datos de entrada no cumplen las
     * validaciones del DTO, indicando qué campo falló y por qué.
     *
     * @param ex excepción lanzada por Spring al fallar {@code @Valid}.
     * @return el detalle del error con estado 400 y el mapa de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Petición con datos inválidos");

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos.");
        problem.setTitle("Datos inválidos");
        problem.setProperty("errors", errors);
        return problem;
    }

}
