package uniVerse.posterPlot.repository;

import uniVerse.posterPlot.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    //회원가입
    public boolean existById(String id);

    public boolean existByEmail(String email);

    public void save(UserEntity user);

    //로그인
    public Optional<UserEntity> findById(String id);

    public String findUserByUserId(Integer userId);
}
