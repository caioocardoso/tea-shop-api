package com.caiooccardoso.tea_shop_api.repositories;

import com.caiooccardoso.tea_shop_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByMailIgnoreCase(String mail);
    boolean existsByCpfAndIdNot(String cpf, UUID id);
    boolean existsByMailIgnoreCaseAndIdNot(String mail, UUID id);
}