package com.spring.libaryapi.Service.impl;

import com.spring.libaryapi.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetent}")
    private String rementent;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message, List<String> mailList) {

        String[] mails = mailList.toArray(new String[mailList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(rementent);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);

    }
}
