package com.urke.saasbackendstarter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DummyEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[DUMMY EMAIL] To: {}\nSubject: {}\nBody:\n{}", to, subject, body);
    }
}