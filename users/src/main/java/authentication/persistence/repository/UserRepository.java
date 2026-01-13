package authentication.persistence.repository;

import authentication.api.model.User;

public interface UserRepository {

    void save(User user);

    User findById(String id);

    User findByEmail(String email);

    User findByUsername(String username);
}