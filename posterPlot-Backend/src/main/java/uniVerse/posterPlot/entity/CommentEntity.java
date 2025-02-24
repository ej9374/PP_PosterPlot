package uniVerse.posterPlot.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private PostEntity post;

    @Column(name = "content")
    private String content;

    public CommentEntity() {}

    public CommentEntity(UserEntity user, PostEntity post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }
}
