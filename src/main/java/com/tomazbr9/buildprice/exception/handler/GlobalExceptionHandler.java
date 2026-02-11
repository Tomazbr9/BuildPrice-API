package com.tomazbr9.buildprice.exception.handler;

import com.tomazbr9.buildprice.dto.exception.ErrorResponseDTO;
import com.tomazbr9.buildprice.exception.*;
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

    @ExceptionHandler(EmptyFileSentException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmptyFileSentException(EmptyFileSentException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TabNotFound.class)
    public ResponseEntity<ErrorResponseDTO> handleTabNotFound(TabNotFound exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RunningJobException.class)
    public ResponseEntity<ErrorResponseDTO> handleRunningJobException(RunningJobException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TempFileCreationFailedException.class)
    public ResponseEntity<ErrorResponseDTO> handleTempFileCreationFailedException(TempFileCreationFailedException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JobExecutionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleJobExecutionNotFoundException(JobExecutionNotFoundException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FailedStopJob.class)
    public ResponseEntity<ErrorResponseDTO> handleFailedStopJob(FailedStopJob exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProjectNotFoundException(ProjectNotFoundException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleItemNotFoundException(ItemNotFoundException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FailedRemovingTempFileException.class)
    public ResponseEntity<ErrorResponseDTO> handleFailedRemovingTempFileException(FailedRemovingTempFileException exception, HttpServletRequest request){
        return buildErrorResponse(exception.getMessage(), request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception exception, HttpServletRequest request){
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
