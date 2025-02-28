package uniVerse.posterPlot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import uniVerse.posterPlot.repository.UserRepository;
import uniVerse.posterPlot.util.RedisUtil;

import java.util.Random;

@Service
public class MailSendService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private int authNumber;

    public MailSendService(JavaMailSender mailSender, UserRepository userRepository, RedisUtil redisUtil) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
    }

    //임의의 6자리 양수를 반환합니다.
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    //mail을 어디서 보내는지, 어디로 보내는지 , 인증 번호를 html 형식으로 어떻게 보내는지 작성합니다.
    public String joinEmail(String email) {

        if (userRepository.existByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        makeRandomNumber();

        String authNum = Integer.toString(authNumber);
        redisUtil.setDataExpire(authNum, email, 300);

        String setFrom = "sexymestudying@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
        String content =
                "PosterPlot을 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요"; //이메일 내용 삽입
        mailSend(setFrom, toMail, title, content);
        return Integer.toString(authNumber);
    }

    //이메일을 전송합니다.
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }
    //인증번호 확인
    public boolean CheckAuthNum(String email, String authNum) {
        String storedEmail = redisUtil.getData(authNum);

        // 🔹 디버깅 로그 추가
        System.out.println("Redis 인증 확인 - 입력된 인증번호: " + authNum + ", Redis 저장 값: " + storedEmail);

        if (storedEmail == null) {
            System.out.println("Redis에서 해당 인증번호가 존재하지 않음!");
            return false;
        } else if (storedEmail.equals(email)) {
            System.out.println("인증 성공!");
            return true;
        } else {
            System.out.println("입력된 이메일과 Redis의 값이 일치하지 않음!");
            return false;
        }
    }


}
