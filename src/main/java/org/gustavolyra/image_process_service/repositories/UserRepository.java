package org.gustavolyra.image_process_service.repositories;

import org.gustavolyra.image_process_service.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);



}
