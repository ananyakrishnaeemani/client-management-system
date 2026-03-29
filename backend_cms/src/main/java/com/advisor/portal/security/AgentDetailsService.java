package com.advisor.portal.security;

import com.advisor.portal.entity.Agent;
import com.advisor.portal.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AgentDetailsService implements UserDetailsService {

    @Autowired
    private AgentRepository agentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Agent agent = agentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found: " + email));
        return new User(agent.getEmail(), agent.getPassword(), Collections.emptyList());
    }
}
