package org.gustavolyra.image_process_service.repositories;

import org.gustavolyra.image_process_service.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);



}
