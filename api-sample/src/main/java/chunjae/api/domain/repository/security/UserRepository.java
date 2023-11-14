package chunjae.api.domain.repository.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import chunjae.api.domain.entity.security.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findUserByUsername(String username);
}
