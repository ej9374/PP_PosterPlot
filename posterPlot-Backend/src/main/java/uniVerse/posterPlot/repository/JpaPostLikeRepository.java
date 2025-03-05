package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.PostEntity;
import uniVerse.posterPlot.entity.PostLikeEntity;
import uniVerse.posterPlot.entity.UserEntity;

@RequiredArgsConstructor
@Repository
public class JpaPostLikeRepository implements PostLikeRepository {

    private final EntityManager em;

    @Override
    public boolean isExistLike(Integer postId, Integer userId) {
        Long count = em.createQuery("select count(pl) from PostLikeEntity pl where pl.post.postId = :postId and pl.user.userId = :userId", Long.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public void addLike(Integer postId, Integer userId) {
        PostLikeEntity postLike = new PostLikeEntity();

        postLike.setPost(em.find(PostEntity.class, postId));
        postLike.setUser(em.find(UserEntity.class, userId));

        em.persist(postLike);
    }
}
