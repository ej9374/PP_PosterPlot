package uniVerse.posterPlot.repository;

import uniVerse.posterPlot.entity.CommentEntity;

import java.util.List;

public interface CommentRepository {

    public void save(CommentEntity comment);

    public CommentEntity findByCommentId(Integer commentId);

    public void delete(Integer commentId);

    public List<Integer> findAllByPostId(Integer postId);


}
