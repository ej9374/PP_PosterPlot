package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.MovieListEntity;

@RequiredArgsConstructor
@Repository
public class JpaMovieListRepostiory implements MovieListRepository {

    private final EntityManager em;

    @Override
    public void save(MovieListEntity movieList) {
        em.persist(movieList);
    }

    @Override
    public MovieListEntity findByMovieListId(Integer movieListId) {
        return em.createQuery("select m from MovieListEntity m where m.movieListId = :movieListId",  MovieListEntity.class)
                .setParameter("movieListId", movieListId)
                .getSingleResult();
    }
}
