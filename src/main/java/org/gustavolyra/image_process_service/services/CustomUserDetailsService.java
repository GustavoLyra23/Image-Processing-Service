package org.gustavolyra.image_process_service.services;

import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        return userRepository.findByEmail(username).orElseThrow(() ->
                {
                    log.error("User not found");
                    return new UsernameNotFoundException("User not found");
                }
        );
    }
}
