package org.gustavolyra.image_process_service.repositories;

import org.gustavolyra.image_process_service.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
