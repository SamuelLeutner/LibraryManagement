package Group.com.library.library.Controller;

import Group.com.library.library.Model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Group.com.library.library.Service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> index() throws IOException {
        return userService.index();
    }

    @PostMapping
    public User create(@RequestBody User user) throws IOException {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User updateBook) throws IOException {
        return userService.update(id, updateBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable int id) throws IOException {
        return userService.delete(id);
    }
}
