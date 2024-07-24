package services;

import camp.woowa.jspcafe.models.User;
import camp.woowa.jspcafe.repository.InMemUserRepository;
import camp.woowa.jspcafe.repository.UserRepository;
import camp.woowa.jspcafe.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemUserRepository();
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateUser() {
        // given
        String userId = "userId";
        String password = "password";
        String name = "name";
        String email = "email";
        // when
        String id = userService.createUser(userId, password, name, email);

        // then
        assertEquals(id, userId);
    }

    @Test
    void testFindAll() {
        // given
        testCreateUser();
        int expected_size = 1;

        // when
        List<User> users = userService.findAll();

        // then
        assertEquals(users.size(), expected_size);
    }

    @Test
    void testFindById() {
        // given
        String userId = "userId";
        String password = "password";
        String name = "name";
        String email = "email";
        userRepository.save(userId, password, name, email);

        // when
        User user = userService.findById(userId);

        // then
        assertEquals(user.getUserId(), userId);
    }
}
