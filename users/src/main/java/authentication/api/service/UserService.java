package authentication.api.service;

import authentication.api.dto.RegisterRequest;
import authentication.api.model.User;

import java.util.Map;

public interface UserService {

    User register(RegisterRequest req);

    Map<String, Object> login(String email, String password);

    User getUserById(String id);

    User getUserByUsername(String username);
}