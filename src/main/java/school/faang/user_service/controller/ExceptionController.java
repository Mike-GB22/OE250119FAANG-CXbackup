package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.exception.DataValidationException;

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
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new ObjectError("testObject", "Test exception"));

        throw new MethodArgumentNotValidException((MethodParameter) null, bindingResult);
    }

    @GetMapping("/ConstraintViolationException")
    public void makeException5() {
//        throw new jakarta.validation.ConstraintViolationException("Test exception");
    }

    @GetMapping("/DataIntegrityViolationException")
    public void makeException6() {
        throw new org.springframework.dao.DataIntegrityViolationException("Test exception");
    }

    @GetMapping("/JDBCException")
    public void makeException7() {
//        throw new org.hibernate.JDBCException("Test exception");
    }

    @GetMapping("/Exception")
    public void makeException8() throws Exception {
        throw new Exception("Test exception");
    }


}
