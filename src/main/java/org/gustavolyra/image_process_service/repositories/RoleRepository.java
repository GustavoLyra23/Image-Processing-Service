package org.gustavolyra.image_process_service.repositories;

import org.gustavolyra.image_process_service.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);


}
