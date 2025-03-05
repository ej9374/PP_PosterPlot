package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.AiStoryEntity;
import uniVerse.posterPlot.entity.MovieListEntity;

@RequiredArgsConstructor
@Repository
public class JpaAiStoryRepository implements AiStoryRepository {

    private final EntityManager em;


    @Override
    public AiStoryEntity findAiStoryById(Integer aiStoryId) {
        return em.createQuery("select a from AiStoryEntity a where a.aiStoryId = :aiStoryId", AiStoryEntity.class)
                .setParameter("aiStoryId", aiStoryId)
                .getSingleResult();
    }

    @Override
    public MovieListEntity findMovieListByAiStory(Integer aiStoryId) {
        return em.createQuery("select a.movieList from AiStoryEntity a where a.aiStoryId = :aiStoryId", MovieListEntity.class)
                .setParameter("aiStoryId", aiStoryId)
                .getSingleResult();
    }

    @Override
    public void save(AiStoryEntity aiStory) { em.persist(aiStory);}

    @Override
    public String findStoryByMovieListId(Integer movieListId) {
        return em.createQuery("select a.story from AiStoryEntity a where a.movieList.movieListId = :movieListId", String.class)
                .setParameter("movieListId", movieListId)
                .getSingleResult();
    }
}
