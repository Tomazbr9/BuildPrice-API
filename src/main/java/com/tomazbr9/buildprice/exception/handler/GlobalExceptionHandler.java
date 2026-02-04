package com.tomazbr9.buildprice.exception.handler;

import com.tomazbr9.buildprice.dto.exception.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception exception, HttpServletRequest request){
        logger.error("Erro não tratado: ", exception);
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(String message, String request, HttpStatus status){
        ErrorResponseDTO error =  new ErrorResponseDTO(
                message,
                status.value(),
                request,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, status);
    }

}
