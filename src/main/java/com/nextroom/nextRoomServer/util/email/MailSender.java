package com.nextroom.nextRoomServer.util.email;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.exceptions.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MailSender {

    private final JavaMailSender emailSender;

    public void sendEmail(String toEmail, String authCode) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, authCode);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.debug("MailSender.sendEmail exception occur toEmail: {}, text: {}", toEmail, authCode);
            throw new CustomException(UNABLE_TO_SEND_EMAIL);
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmailForm(String toEmail, String authCode) {
        String text = "\uD83D\uDD11 회원 가입을 위해 아래의 인증 코드를 화면에 입력해주세요. 인증코드: " + authCode;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("방탈출 힌트폰 서비스 '넥스트룸' 이메일 인증 번호 입니다.");
        message.setText(text);

        return message;
    }
}
