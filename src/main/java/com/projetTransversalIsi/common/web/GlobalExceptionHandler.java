package com.projetTransversalIsi.common.web;


import com.projetTransversalIsi.common.domain.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400,ex.getMessage(),System.currentTimeMillis()));
    }

    @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Une erreur interne est survenue", System.currentTimeMillis()));

    }
}
