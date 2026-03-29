package com.advisor.portal.service;

import com.advisor.portal.dto.AuthResponse;
import com.advisor.portal.dto.LoginRequest;
import com.advisor.portal.dto.SignupRequest;
import com.advisor.portal.entity.Agent;
import com.advisor.portal.repository.AgentRepository;
import com.advisor.portal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse signup(SignupRequest request) {
        if (agentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Agent agent = new Agent();
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agentRepository.save(agent);

        String token = jwtUtil.generateToken(agent.getEmail());
        return new AuthResponse(token, agent.getName(), agent.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        Agent agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(agent.getEmail());
        return new AuthResponse(token, agent.getName(), agent.getEmail());
    }
}
