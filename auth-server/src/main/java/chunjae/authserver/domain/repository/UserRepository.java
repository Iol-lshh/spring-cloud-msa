package chunjae.authserver.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import chunjae.authserver.domain.entity.User;


public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findUserByUsername(String username);
}
