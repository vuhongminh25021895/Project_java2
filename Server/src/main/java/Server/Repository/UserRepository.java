package Server.Repository;

import Server.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends JpaRepository<User, String> {
    User findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
