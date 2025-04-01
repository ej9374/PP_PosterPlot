package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.Genre;
import uniVerse.posterPlot.entity.PostEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JpaPostRepository implements PostRepository {

    private final EntityManager em;

    @Override
    public void save(PostEntity post) { em.persist(post); }

    @Override
    public PostEntity findByPostId(Integer postId) {
        return em.createQuery("select p from PostEntity p where p.postId = :postId", PostEntity.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    @Override
    public List<Integer> findAllRecent() {
        return em.createQuery("select p.postId from PostEntity p order by p.postId desc", Integer.class)
                .getResultList();
    }

    @Override
    public List<Integer> findAllOldest() {
        return em.createQuery("select p.postId from PostEntity p order by p.postId asc", Integer.class)
                .getResultList();
    }

    @Override
    public List<Integer> findAllByLikes() {
        return em.createQuery("select p.postId from PostEntity p order by p.totalLikes desc, p.postId desc", Integer.class)
                .getResultList();
    }

    @Override
    public List<Integer> findTopLikesPost() {
        return em.createQuery("select p.postId from PostEntity p where p.totalLikes >= 10 order by p.totalLikes desc, p.postId desc", Integer.class)
                .setMaxResults(3)
                .getResultList();
    }

    @Override
    public List<Integer> findAllByUserId(Integer userId) {
        return em.createQuery("select p.postId from PostEntity p where p.user.userId = :userId order by p.postId desc", Integer.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Integer> findAllByGenre(Genre genre) {
        return em.createQuery("select p.postId from PostEntity p where p.genre = :genre order by p.postId desc", Integer.class)
                .setParameter("genre", genre)
                .getResultList();
    }

    @Override
    public void delete(Integer postId) {
        PostEntity post = findByPostId(postId);
        if (post != null) {
            em.createQuery("delete from CommentEntity c where c.post.postId = :postId")
                    .setParameter("postId", postId)
                    .executeUpdate();
            em.createQuery("delete from PostLikeEntity pl where pl.post.postId = :postId")
                    .setParameter("postId", postId)
                    .executeUpdate();
            em.remove(post);
        }
    }

    @Override
    public List<String> findTitlesByPostIds(List<Integer> postIds) {
        Map<Integer, String> postIdToTitle = em.createQuery(
                        "select p.postId, p.title from PostEntity p where p.postId in :postIds", Object[].class)
                .setParameter("postIds", postIds)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> (String) result[1]
                ));

        return postIds.stream()
                .map(postIdToTitle::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> findUsersByPostIds(List<Integer> postIds) {
        Map<Integer, Integer> postIdToUserId = em.createQuery(
                        "select p.postId, p.user.userId from PostEntity p where p.postId in :postIds", Object[].class)
                .setParameter("postIds", postIds)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> (Integer) result[1]
                ));

        return postIds.stream()
                .map(postIdToUserId::get)
                .collect(Collectors.toList());
    }


    @Override
    public Integer findAiStoryId(Integer postId) {
        return em.createQuery("select p.aiStory from PostEntity p where p.postId = :postId", Integer.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }
}
