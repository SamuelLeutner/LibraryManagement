package Group.com.library.library.Service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import Group.com.library.library.Model.User;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import Group.com.library.library.Enum.UserRoleEnum;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final String USER_FILE_PATH = "src/database/users.csv";

    public List<User> index() throws IOException {
        List<User> users = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.length < 6 || line[0].isEmpty()) continue;

                User user = new User();
                user.setId(Integer.parseInt(line[0]));
                user.setName(line[1]);
                user.setEmail(line[2]);
                user.setPassword(line[3]);
                user.setPhone(line[4]);
                user.setRole(UserRoleEnum.valueOf(line[5]));

                users.add(user);
            }
        } catch (Exception e) {
            throw new IOException("Erro ao carregar usuários.", e);
        }

        return users;
    }

    public User create(User user) throws IOException {
        File file = new File(USER_FILE_PATH);
        boolean fileExists = file.exists();

        if (!fileExists) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(USER_FILE_PATH, true))) {
            if (!fileExists) {
                writer.writeNext(new String[]{"ID", "Name", "Email", "Password", "Phone", "Role"});
            }

            writer.writeNext(new String[]{
                    String.valueOf(user.getId()),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getPhone(),
                    user.getRole().name()
            });
        }

        return user;
    }

    public ResponseEntity<User> update(int id, User updatedUser) throws IOException {
        List<User> users = index();
        boolean updated = false;

        for (User user : users) {
            if (user.getId() == id) {
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                user.setPassword(updatedUser.getPassword());
                user.setPhone(updatedUser.getPhone());
                user.setRole(updatedUser.getRole());
                updated = true;
                break;
            }
        }

        if (!updated) throw new RuntimeException("Usuário não encontrado.");

        try (CSVWriter writer = new CSVWriter(new FileWriter(USER_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "Name", "Email", "Password", "Phone", "Role"});
            for (User user : users) {
                writer.writeNext(new String[]{
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhone(),
                        user.getRole().name()
                });
            }
        }

        return ResponseEntity.ok(updatedUser);
    }

    public ResponseEntity<User> delete(int id) throws IOException {
        List<User> users = index();
        User userToDelete = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);

        if (userToDelete == null) throw new RuntimeException("Usuário não encontrado.");

        users.removeIf(u -> u.getId() == id);

        try (CSVWriter writer = new CSVWriter(new FileWriter(USER_FILE_PATH, false))) {
            writer.writeNext(new String[]{"ID", "Name", "Email", "Password", "Phone", "Role"});
            for (User user : users) {
                writer.writeNext(new String[]{
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhone(),
                        user.getRole().name()
                });
            }
        }

        return ResponseEntity.ok().build();
    }
}
