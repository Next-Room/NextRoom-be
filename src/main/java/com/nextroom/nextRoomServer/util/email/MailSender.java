package com.nextroom.nextRoomServer.util.email;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.exceptions.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String MAIL_SUBJECT = "방탈출 힌트폰 서비스 '넥스트룸' 이메일 인증 번호 입니다.";

    public void sendEmail(String toEmail, String authCode) {
        MimeMessagePreparator emailHtmlForm = createHtmlEmailForm(toEmail, authCode);
        try {
            emailSender.send(emailHtmlForm);
        } catch (RuntimeException e) {
            log.debug("MailSender.sendEmail exception occur toEmail: {}, text: {}", toEmail, authCode);
            throw new CustomException(UNABLE_TO_SEND_EMAIL);
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail, String authCode) {
        String text = "\uD83D\uDD11 회원 가입을 위해 아래의 인증 코드를 화면에 입력해주세요. 인증코드: " + authCode;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(MAIL_SUBJECT);
        message.setText(text);

        return message;
    }

    private MimeMessagePreparator createHtmlEmailForm(String toEmail, String authCode) {
        Context context = new Context();
        context.setVariable("authCode", authCode);

        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String content = templateEngine.process("index.html", context);

            helper.setTo(toEmail);
            helper.setSubject(MAIL_SUBJECT);
            helper.setText(content, true);
        };
    }
}
