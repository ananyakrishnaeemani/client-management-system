package com.advisor.portal.service;

import com.advisor.portal.dto.ClientDTO;
import com.advisor.portal.entity.Agent;
import com.advisor.portal.entity.Client;
import com.advisor.portal.exception.ResourceNotFoundException;
import com.advisor.portal.repository.AgentRepository;
import com.advisor.portal.repository.ClientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgentRepository agentRepository;

    private String getCurrentAgentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findByAgentEmail(getCurrentAgentEmail())
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findByIdAndAgentEmail(id, getCurrentAgentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return convertToDTO(client);
    }

    public ClientDTO createClient(ClientDTO clientDTO) {
        String email = getCurrentAgentEmail();
        Agent agent = agentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        Client client = convertToEntity(clientDTO);
        client.setAgent(agent);
        client.setRiskCategory(calculateRiskCategory(client));
        return convertToDTO(clientRepository.save(client));
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findByIdAndAgentEmail(id, getCurrentAgentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        BeanUtils.copyProperties(clientDTO, client, "id", "documentData", "documentName", "agent");
        client.setRiskCategory(calculateRiskCategory(client));
        return convertToDTO(clientRepository.save(client));
    }

    public void deleteClient(Long id) {
        Client client = clientRepository.findByIdAndAgentEmail(id, getCurrentAgentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        clientRepository.delete(client);
    }

    public void uploadDocument(Long id, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        Client client = clientRepository.findByIdAndAgentEmail(id, getCurrentAgentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        client.setDocumentName(file.getOriginalFilename());
        client.setDocumentData(file.getBytes());
        clientRepository.save(client);
    }

    public Client getClientEntity(Long id) {
        return clientRepository.findByIdAndAgentEmail(id, getCurrentAgentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    private String calculateRiskCategory(Client client) {
        String tolerance = client.getRiskTolerance();
        String reaction = client.getMarketDropReaction();
        String experience = client.getInvestmentExperience();

        if (reaction == null && experience == null) {
            Double amt = client.getInvestmentAmount();
            Integer duration = client.getInvestmentDuration();
            if (tolerance != null && tolerance.equalsIgnoreCase("High") && amt != null && amt > 100000) return "Aggressive";
            else if (tolerance != null && tolerance.equalsIgnoreCase("Low")) return "Conservative";
            else if (amt != null && amt > 50000 && duration != null && duration > 60) return "Moderate";
            else if (tolerance != null && tolerance.equalsIgnoreCase("High")) return "Moderate";
            return "Conservative";
        }

        int score = 0;
        if (tolerance != null) {
            if (tolerance.equalsIgnoreCase("High")) score += 3;
            else if (tolerance.equalsIgnoreCase("Medium")) score += 2;
            else score += 1; // Low
        }
        if (reaction != null) {
            if (reaction.contains("Buy") || reaction.contains("Invest")) score += 3;
            else if (reaction.contains("Hold") || reaction.contains("Wait")) score += 2;
            else score += 1; // Sell / Panic
        }
        if (experience != null) {
            if (experience.contains("Advanced") || experience.contains("Professional")) score += 3;
            else if (experience.contains("Intermediate") || experience.contains("Some")) score += 2;
            else score += 1; // None / Beginner
        }

        if (score >= 7) return "Aggressive";
        if (score >= 5) return "Moderate";
        return "Conservative";
    }

    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(client, dto);
        return dto;
    }

    private Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        BeanUtils.copyProperties(dto, client);
        return client;
    }
}
