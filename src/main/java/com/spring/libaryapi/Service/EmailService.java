package com.spring.libaryapi.Service;

import java.util.List;
import java.lang.String;

public interface EmailService {


    void sendMails(String message, List<String> mailList);
}
