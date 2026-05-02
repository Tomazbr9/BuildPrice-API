package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.client.ClientRequestDTO;
import com.tomazbr9.buildprice.dto.client.ClientResponseDTO;
import com.tomazbr9.buildprice.entity.Client;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.repository.ClientRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ClientResponseDTO> getClients(UUID userId){

        List<Client> clients = clientRepository.findByUserId(userId);

        return clients.stream().map(
                client -> new ClientResponseDTO(
                        client.getId(),
                        client.getName(),
                        client.getCpfCnpj(),
                        client.getPhone(),
                        client.getEmail()
                )
        ).toList();
    }

    public ClientResponseDTO addClient(ClientRequestDTO request, UUID userId){

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Client client = Client.builder()
                .name(request.name())
                .cpfCnpj(request.cpfCnpj())
                .phone(request.phone())
                .email(request.email())
                .user(user)
                .build();

        Client clientSaved = clientRepository.save(client);

        return new ClientResponseDTO(
                clientSaved.getId(),
                clientSaved.getName(),
                clientSaved.getCpfCnpj(),
                clientSaved.getPhone(),
                clientSaved.getEmail()
        );

    }



}
