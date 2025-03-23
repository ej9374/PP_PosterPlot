package uniVerse.posterPlot.repository;

import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.Genre;
import uniVerse.posterPlot.entity.PostEntity;

import java.util.List;

public interface PostRepository {

    public void save(PostEntity post);

    public PostEntity findByPostId(Integer postId);

    public List<Integer> findAllRecent();

    public List<Integer> findAllOldest();

    public List<Integer> findAllByLikes();

    public List<Integer> findTopLikesPost();

    public List<Integer> findAllByUserId(Integer userId);

    public List<Integer> findAllByGenre(Genre genre);

    public void delete(Integer postId);

    public List<String> findTitlesByPostIds(List<Integer> postIds);

    public List<Integer> findUsersByPostIds(List<Integer> postIds);

    public Integer findAiStoryId(Integer postId);
}
