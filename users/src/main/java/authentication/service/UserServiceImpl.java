package authentication.service;


import authentication.api.dto.RegisterRequest;
import authentication.api.model.User;
import authentication.api.service.UserService;
import authentication.persistence.repository.UserRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.HashMap;
import java.util.Map;

// 1. Tell OSGi this is a service
@Component(service = UserService.class)
public class UserServiceImpl implements UserService {
    // 2. Tell OSGi to inject the repository
    @Reference
    private UserRepository userRepository;
    // 3. OSGi needs a zero-arg constructor or specific @Activate constructor
    public UserServiceImpl() {
    }
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public User register(RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new RuntimeException("User already exists!");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPhonenumber(req.getPhonenumber());
        user.setRole(req.getRole());

        String hashedPassword = passwordEncoder.encode(req.getPassword());
        user.setPassword(hashedPassword); // hashing omitted for simplicity

        userRepository.save(user);
        return user;
    }

    @Override
    public Map<String, Object> login(String email, String password) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("user", user);
        return res;
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}