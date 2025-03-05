package uniVerse.posterPlot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uniVerse.posterPlot.entity.UserEntity;
import uniVerse.posterPlot.service.MovieService;
import uniVerse.posterPlot.util.SecurityUtil;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> files) {
        UserEntity user = SecurityUtil.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");

        if (files == null || files.size() != 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일은 반드시 2개를 업로드 해야 합니다.");
        }

        Integer movieListId = movieService.uploadMovieImage(user, files);
        return ResponseEntity.ok("이미지가 성공적으로 업로드 되었습니다. movieListId = " + movieListId);
    }

    @PostMapping("/getStory")
    public ResponseEntity<String> sendToFlask(@RequestParam("movieListId") Integer movieListId) {
        UserEntity user = SecurityUtil.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 유저가 아닙니다.");

        String story = movieService.sendMovieListToFlask(movieListId);
        return ResponseEntity.ok(story);
    }
}
