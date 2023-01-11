package com.webmuseum.museum.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.models.EmailDetails;
import com.webmuseum.museum.service.IEmailService;

@Service
public class EmailServiceImpl implements IEmailService {
 
        @Autowired 
        private JavaMailSender mailSender;
     
        @Value("${spring.mail.username}") 
        private String sender;
     
        public boolean sendSimpleMail(EmailDetails details)
        {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(sender);

                mailMessage.setTo(details.getRecipient());
                mailMessage.setText(details.getBody());
                mailMessage.setSubject(details.getSubject());
     
                mailSender.send(mailMessage);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
    
}
