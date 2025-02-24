package uniVerse.posterPlot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "total_likes")
    private Integer totalLikes;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Genre genre;

    public PostEntity() {}

    public PostEntity(UserEntity user, String title, String content, Integer totalLikes, Genre genre) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.totalLikes = totalLikes;
        this.genre = genre;
    }
}
