package ra.project_api.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ra.project_api.constrants.EHttpStatus;
import ra.project_api.dto.response.ResponseWrapper;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> doHandler(RuntimeException e) {
        Map<String, String> map = new HashMap<>();
        map.put("message", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> accessDenied(AccessDeniedException e) {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Bạn không có quyền truy cập");
        return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseWrapper<String>> handleNoSuchElementException(NoSuchElementException e) {
        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .eHttpStatus(EHttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .data(e.getMessage())
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        ResponseWrapper<Map<String, String>> responseWrapper = ResponseWrapper.<Map<String, String>>builder()
                .eHttpStatus(EHttpStatus.FAILED)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .data(errors)
                .build();

        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }
}
