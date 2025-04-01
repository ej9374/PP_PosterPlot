package uniVerse.posterPlot.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uniVerse.posterPlot.dto.PostListHomeResponseDto;
import uniVerse.posterPlot.dto.PostListResponseDto;
import uniVerse.posterPlot.dto.PostRequestDto;
import uniVerse.posterPlot.dto.PostResponseDto;
import uniVerse.posterPlot.entity.*;
import uniVerse.posterPlot.repository.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final AiStoryRepository aiStoryRepository;

    // Ai story 띄우기
    @Transactional
    public AiStoryEntity getAiStory(Integer aiStoryId){
        AiStoryEntity aiStory = aiStoryRepository.findAiStoryById(aiStoryId);

        if (aiStory == null){
            throw new RuntimeException("Ai Story를 찾을 수 없습니다.");
        }
        return aiStory;
    }

    // 영화 포스터 띄우기
    @Transactional
    public List<String> getMovieList(Integer aiStoryId){
        MovieListEntity movieList = aiStoryRepository.findMovieListByAiStory(aiStoryId);
        String movie1stPath = movieList.getMovie1stPath();
        String movie2ndPath = movieList.getMovie2ndPath();

        if ( movie1stPath == null || movie2ndPath == null){
            throw new RuntimeException("영화 포스터를 찾을 수 없습니다.");
        }

        List<String> movies = new ArrayList<>();
        movies.add(movie1stPath);
        movies.add(movie2ndPath);

        return movies;
    }

    // 글 작성 메서드
    @Transactional
    public void createPost(UserEntity user, PostRequestDto requestDto) {

        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        Integer totalLike = 0;
        Genre genre = requestDto.getGenre();

        AiStoryEntity aiStory = null;

        if (requestDto.getAiStoryId() != null) {
            aiStory = aiStoryRepository.findAiStoryById(requestDto.getAiStoryId());
        }

        PostEntity post = new PostEntity(user, title, content, totalLike, genre, aiStory);
        postRepository.save(post);
    }

    // 전달받은 postId로 postEntity 찾아서 전부 반환
    public PostResponseDto getPost(Integer postId) {
        PostEntity post = postRepository.findByPostId(postId);

        if (post == null) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다. postId: " + postId);
        }

        AiStoryEntity aiStory = post.getAiStory();
        MovieListEntity movieList = (aiStory != null) ? aiStory.getMovieList() : null;

        String movie1stPath = (movieList != null) ? movieList.getMovie1stPath() : "";
        String movie2ndPath = (movieList != null) ? movieList.getMovie2ndPath() : "";
        String story = (aiStory != null) ? aiStory.getStory() : "";

        PostResponseDto responseDto = new PostResponseDto(
                postId,
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getTotalLikes(),
                post.getGenre(),
                story,
                movie1stPath,
                movie2ndPath);
        if (responseDto == null) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다. postId: " + postId);
        }
        return responseDto;
    }

    // 모든 글 조회하기 (최신순 / postId 내림차순)
    public List<PostListResponseDto> getPostsLatest(){
        List<Integer> postIds = postRepository.findAllRecent();
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i)));
        }
        return responseList;
    }

    // 오래된순 (postId 오름차순)
    public List<PostListResponseDto> getPostsOldest(){
        List<Integer> postIds = postRepository.findAllOldest();
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i)));
        }
        return responseList;
    }

    // 좋아요순
    public List<PostListResponseDto> getPostsByLikes(){
        List<Integer> postIds = postRepository.findAllByLikes();
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i)));
        }
        return responseList;
    }

    // 상위 좋아요 3개의 게시글 띄우기 (좋아요 10개 이상의 글 들중)
    public List<PostListResponseDto> getPostsTopLikes(){
        List<Integer> postIds = postRepository.findTopLikesPost();
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i)));
        }
        return responseList;
    }

    //home
    public List<PostListHomeResponseDto> getPostsHomeTopLikes(){
        List<Integer> postIds = postRepository.findTopLikesPost();
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<MovieListEntity> movieLists = getMovieListsByPostIds(postIds);

        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListHomeResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListHomeResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i), movieLists.get(i).getMovie1stPath(), movieLists.get(i).getMovie2ndPath()));
        }
        return responseList;
    }

    // 유저별 글 조회하기 -> 마이페이지
    public List<PostListResponseDto> getPostsByUser(Integer userId){
        List<Integer> postIds = postRepository.findAllByUserId(userId);
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        String user = userRepository.findUserByUserId(userId);
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userId, user));
        }
        return responseList;
    }

    // 장르별 글 조회
    public List<PostListResponseDto> getPostsByGenre(Genre genre){
        List<Integer> postIds = postRepository.findAllByGenre(genre);
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<String> titles = getTitlesByPostIds(postIds);
        List<Integer> userIds = getUserIdsByPostIds(postIds);
        List<String> users = new ArrayList<>();
        for (int i = 0; i< userIds.size(); i++){
            users.add(userRepository.findUserByUserId(userIds.get(i)));
        }
        List<PostListResponseDto> responseList = new ArrayList<>();

        for (int i = 0; i < postIds.size(); i++) {
            responseList.add(new PostListResponseDto(postIds.get(i), titles.get(i), userIds.get(i), users.get(i)));
        }
        return responseList;
    }

    // 글 삭제하기 (사용자 토큰 필터 확인 필요)
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        PostEntity post = postRepository.findByPostId(postId);

        if (post == null) {
            throw new RuntimeException("해당 게시글을 찾을 수 없습니다.");
        }
        if (!post.getUser().getUserId().equals(userId)){
            throw new RuntimeException("게시글 삭제 권한이 없습니다.");
        }
        postRepository.delete(postId);
    }

    public List<String> getTitlesByPostIds(List<Integer> postIds){
        return postRepository.findTitlesByPostIds(postIds);
    }

    public List<Integer> getUserIdsByPostIds(List<Integer> postIds){
        return postRepository.findUsersByPostIds(postIds);
    }

    public List<MovieListEntity> getMovieListsByPostIds(List<Integer> postIds){
        List<MovieListEntity> movieLists = new ArrayList<>();
        for (Integer postId : postIds){
            MovieListEntity movieList = aiStoryRepository.findMovieListByAiStory(postRepository.findAiStoryId(postId));
            movieLists.add(movieList);
        }
        return movieLists;
    }

    // 게시글 좋아요 기능
    @Transactional
    public void likePost(Integer postId, Integer userId) {
        if (postLikeRepository.isExistLike(postId, userId)){
            throw new RuntimeException("이미 좋아요를 누르셨습니다.");
        }
        postLikeRepository.addLike(postId, userId);

        PostEntity post = postRepository.findByPostId(postId);
        if (post == null){
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
        post.setTotalLikes(post.getTotalLikes() + 1);
        postRepository.save(post);
    }
}
