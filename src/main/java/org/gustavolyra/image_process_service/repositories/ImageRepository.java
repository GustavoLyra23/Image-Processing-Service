package org.gustavolyra.image_process_service.repositories;

import org.gustavolyra.image_process_service.models.entities.Image;
import org.gustavolyra.image_process_service.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    Page<Image> findByUser(User user, Pageable pageable);

}
