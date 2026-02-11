package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.user.UserPatchDTO;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserResponseDTO getUser(String username){

        User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return new UserResponseDTO(user.getEmail());
    }

    public UserResponseDTO putUser(UserPatchDTO request, String email){

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (request.email() != null && !request.email().isBlank()){
            user.setEmail(request.email());
        }

        if(request.password() != null && !request.password().isBlank()){
            user.setPassword(request.password());
        }

        User userUpdate = userRepository.save(user);

        return new UserResponseDTO(userUpdate.getEmail());
    }

    public void deleteUser(String email){

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        userRepository.delete(user);

    }

}
