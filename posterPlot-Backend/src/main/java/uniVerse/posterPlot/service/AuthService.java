package uniVerse.posterPlot.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uniVerse.posterPlot.dto.LoginRequestDto;
import uniVerse.posterPlot.dto.SignUpRequestDto;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 아이디 중복 체크
    public boolean isExist(String id) { return userRepository.existById(id); }

    // 비밀번호 확인
    public boolean isCorrectPassword(String password, String confirmPassword) { return password.equals(confirmPassword); }

    // 회원가입
    @Transactional
    public void signUp(SignUpRequestDto requestDto) {

        String id = requestDto.getId();
        String password = requestDto.getPassword();
        String email = requestDto.getEmail();

        //비밀번호 해시
        String hashPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity(id, hashPassword, email);
        userRepository.save(user);
    }

    // 로그인
    @Transactional
    public void login(LoginRequestDto requestDto) {

        String id = requestDto.getId();
        String password = requestDto.getPassword();

        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        boolean check = passwordEncoder.matches(password, user.getPassword());

        if (!check) {
            throw new IllegalArgumentException("비밀번호가 옳지 않습니다.");
        }
    }
}
