package uniVerse.posterPlot.repository;

public interface PostLikeRepository {

    public boolean isExistLike(Integer postId, Integer userId);

    public void addLike(Integer postId, Integer userId);
}
