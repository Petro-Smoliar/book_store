package com.example.book.store.repository;

import com.example.book.store.model.Role;
import com.example.book.store.model.User;
import com.example.book.store.repository.user.UserRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_user_to_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_role_to_roles_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/"
                                          + "add_user_id_and_role_id_to_user_roles_table.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    void findByEmail_ShouldReturnUserWithRoles() {
        // Given
        String userEmail = "test@user.com";
        // When
        Optional<User> foundUserOptional = userRepository.findByEmail(userEmail);
        // Then
        Assertions.assertTrue(foundUserOptional.isPresent());
        User foundUser = foundUserOptional.get();
        Assertions.assertEquals(userEmail, foundUser.getEmail());
        Assertions.assertFalse(foundUser.getRoles().isEmpty());
        Assertions.assertEquals(
                Role.RoleName.USER,
                foundUser.getRoles().iterator().next().getName());
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptionalForNonexistentEmail() {
        // Given
        String nonexistentEmail = "nonexistent@example.com";

        // When
        Optional<User> foundUserOptional = userRepository.findByEmail(nonexistentEmail);

        // Then
        Assertions.assertTrue(foundUserOptional.isEmpty());
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_all_from_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_all_from_user_roles_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_all_from_roles_table.sql"));
        }
    }
}
