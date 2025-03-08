package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.exception.DataValidationException;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Set;

@Controller
@RequestMapping("/exceptions")
public class ExceptionController {
    
    @GetMapping("/DataValidationException")
    public void makeException1() {
        throw new DataValidationException("Test exception");
    }

    @GetMapping("/EntityNotFoundException")
    public void makeException2() {
        throw new EntityNotFoundException("Test exception");
    }

    @GetMapping("/IllegalArgumentException")
    public void makeException3() {
        throw new IllegalArgumentException("Test exception");
    }

    @GetMapping("/MethodArgumentNotValidException")
    public void makeException4() throws MethodArgumentNotValidException {
        throw getMethodArgumentNotValidException();
    }

    @GetMapping("/ConstraintViolationException")
    public void makeException5() {
        throw new jakarta.validation.ConstraintViolationException(Set.of());
    }

    @GetMapping("/DataIntegrityViolationException")
    public void makeException6() {
        throw new org.springframework.dao.DataIntegrityViolationException("Test exception");
    }

    @GetMapping("/JDBCException")
    public void makeException7() {
        throw new org.hibernate.JDBCException("Test exception", new SQLException());
    }

    @GetMapping("/SQLException")
    public void makeException8() throws Exception {
        throw new SQLException("Test exception");
    }

    @GetMapping("/Exception")
    public void makeException9() throws Exception {
        throw new Exception("Test exception");
    }


    private static MethodArgumentNotValidException getMethodArgumentNotValidException() {
        Method method = null;
        try {
            method = ExceptionController.class.getDeclaredMethod("method", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        MethodParameter methodParameter = new MethodParameter(method, 0);

        BindException bindException = new BindException(new BeanPropertyBindingResult(null, "BindObject"));

        return new MethodArgumentNotValidException(methodParameter, bindException.getBindingResult());
    }

    private void method(String str) {}

}
