package uniVerse.posterPlot.repository;

import uniVerse.posterPlot.entity.AiStoryEntity;
import uniVerse.posterPlot.entity.MovieListEntity;

public interface AiStoryRepository {

    public AiStoryEntity findAiStoryById(Integer aiStoryId);

    public MovieListEntity findMovieListByAiStory(Integer aiStoryId);
}
