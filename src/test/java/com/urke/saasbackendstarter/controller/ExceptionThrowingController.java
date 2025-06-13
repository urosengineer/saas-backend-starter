package com.urke.saasbackendstarter.controller;

import com.urke.saasbackendstarter.exception.UserAlreadyExistsException;
import com.urke.saasbackendstarter.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.Hidden
@RestController
@RequestMapping("/test-ex")
public class ExceptionThrowingController {
    @GetMapping("/notfound")
    public void throwNotFound() {
        throw new UserNotFoundException("User test not found.");
    }

    @GetMapping("/exists")
    public void throwAlreadyExists() {
        throw new UserAlreadyExistsException("User already exists.");
    }
}