package uniVerse.posterPlot.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uniVerse.posterPlot.entity.UserEntity;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaUserRepository implements UserRepository{

    private final EntityManager em;

    @Override
    public boolean existById(String id){
        return em.createQuery("select count(u) > 0 from UserEntity u where u.id = :id", Boolean.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public boolean existByEmail(String email){
        return em.createQuery("select count(u) > 0 from UserEntity u where u.email = :email", Boolean.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public void save(UserEntity user){
        em.persist(user);
    }

    @Override
    public Optional<UserEntity> findById(String id){
        try {
            return Optional.ofNullable(em.createQuery("select u from UserEntity u where u.id = :id", UserEntity.class)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public String findUserByUserId(Integer userId) {
        return em.createQuery("select u.id from UserEntity u where u.userId = :userId", String.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}

