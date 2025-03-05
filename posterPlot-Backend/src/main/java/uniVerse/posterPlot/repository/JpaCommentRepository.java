package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.CommentEntity;
import uniVerse.posterPlot.entity.PostEntity;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager em;

    @Override
    public void save(CommentEntity comment) {
        em.persist(comment);
    }

    @Override
    public CommentEntity findByCommentId(Integer commentId) {
    return em.createQuery("select c from CommentEntity c where c.commentId = :commentId", CommentEntity.class)
                .setParameter("commentId", commentId)
                .getSingleResult();
    }

    @Override
    public void delete(Integer commentId) {
        CommentEntity comment = findByCommentId(commentId);
        if (comment != null) {
            em.remove(comment);
        }
    }

    @Override
    public List<Integer> findAllByPostId(Integer postId) {
        return em.createQuery("select c.commentId from CommentEntity c where c.post.postId = :postId order by c.commentId desc", Integer.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
