package com.tomazbr9.buildprice.integration.repository;

import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should save user with role")
    void shouldSaveUserWithRole() {

        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow();

        User user = new User();
        user.setEmail("bruno@email.com");
        user.setPassword("123456");
        user.setRoles(Set.of(role));

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("bruno@email.com", saved.getEmail());
        assertEquals(1, saved.getRoles().size());
    }

    @Test
    @DisplayName("Should not allow duplicate email")
    void shouldNotAllowDuplicateEmail() {

        User user1 = new User();
        user1.setEmail("duplicado@email.com");
        user1.setPassword("123");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("duplicado@email.com");
        user2.setPassword("456");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Should load roles eagerly")
    void shouldLoadRolesEagerly() {

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow();

        User user = new User();
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user.setRoles(Set.of(role));

        user = userRepository.save(user);

        User found = userRepository.findById(user.getId()).orElseThrow();

        assertFalse(found.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Should remove entries from join table when user is deleted")
    void shouldRemoveJoinTableEntriesOnDelete() {

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow();

        User user = new User();
        user.setEmail("delete@email.com");
        user.setPassword("123");
        user.setRoles(Set.of(role));

        user = userRepository.save(user);

        UUID userId = user.getId();

        userRepository.delete(user);
        userRepository.flush();

        assertFalse(userRepository.findById(userId).isPresent());
    }

}

