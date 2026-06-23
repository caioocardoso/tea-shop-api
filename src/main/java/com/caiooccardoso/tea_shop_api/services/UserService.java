package com.caiooccardoso.tea_shop_api.services;

import com.caiooccardoso.tea_shop_api.dto.UserDTO;
import com.caiooccardoso.tea_shop_api.exceptions.UserAlreadyExistsException;
import com.caiooccardoso.tea_shop_api.exceptions.UserNotFoundException;
import com.caiooccardoso.tea_shop_api.models.User;
import com.caiooccardoso.tea_shop_api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(UserDTO userDTO) {
        validateConflictsForCreate(userDTO);

        User user = new User();
        applyChanges(user, userDTO);

        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserById(UUID id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        validateConflictsForUpdate(id, userDTO);
        applyChanges(existingUser, userDTO);

        return userRepository.save(existingUser);
    }

    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    }

    private void validateConflictsForCreate(UserDTO userDTO) {
        if (userRepository.existsByCpf(userDTO.getCpf())) {
            throw new UserAlreadyExistsException("Já existe um usuário com este CPF");
        }

        if (userRepository.existsByMailIgnoreCase(userDTO.getMail())) {
            throw new UserAlreadyExistsException("Já existe um usuário com este e-mail");
        }
    }

    private void validateConflictsForUpdate(UUID id, UserDTO userDTO) {
        if (userRepository.existsByCpfAndIdNot(userDTO.getCpf(), id)) {
            throw new UserAlreadyExistsException("Já existe um usuário com este CPF");
        }

        if (userRepository.existsByMailIgnoreCaseAndIdNot(userDTO.getMail(), id)) {
            throw new UserAlreadyExistsException("Já existe um usuário com este e-mail");
        }
    }

    private void applyChanges(User user, UserDTO userDTO) {
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setCpf(userDTO.getCpf());
        user.setMail(userDTO.getMail());
        user.setBirthDate(userDTO.getBirthDate());
        user.setAddressList(userDTO.getAddressList());
    }
}