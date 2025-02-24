package uniVerse.posterPlot.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movie")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "movie_1st_path")
    private String movie1stPath;

    @Column(name = "movie_2nd_path")
    private String movie2ndPath;

    public MovieEntity() {}

    public MovieEntity(String movie1stPath, String movie2ndPath) {
        this.movie1stPath = movie1stPath;
        this.movie2ndPath = movie2ndPath;
    }
}
