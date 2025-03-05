package uniVerse.posterPlot.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "movie_list")
public class MovieListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_list_id")
    private Integer movieListId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name = "movie_1st_path")
    private String movie1stPath;

    @Column(name = "movie_2nd_path")
    private String movie2ndPath;

    public MovieListEntity() {}

    public MovieListEntity(UserEntity user, String movie1stPath, String movie2ndPath) {
        this.user = user;
        this.movie1stPath = movie1stPath;
        this.movie2ndPath = movie2ndPath;
    }
}
