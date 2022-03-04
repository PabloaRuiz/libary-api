package com.spring.libaryapi.Service;

import java.util.List;

public interface EmailService {


    void sendMails(String message, List<String> mailList);
}
