package com.webmuseum.museum.service;

import com.webmuseum.museum.models.EmailDetails;

public interface IEmailService {

    boolean sendSimpleMail(EmailDetails details);

}
