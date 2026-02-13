package com.tomazbr9.buildprice.util;

import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

public class TestUserFactory {

    private static final String EMAIL_DEFAULT = "user@email.com";

    public static User createDefaultUser(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_USER)));

        User user = new User(
                EMAIL_DEFAULT,
                passwordEncoder.encode("123456"),
                Set.of(role)
        );

        return userRepository.save(user);
    }
}

