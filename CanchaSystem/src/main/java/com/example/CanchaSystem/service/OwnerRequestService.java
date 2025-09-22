package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.requests.NoRequestsException;
import com.example.CanchaSystem.exception.client.ClientAlreadyRequestedException;
import com.example.CanchaSystem.exception.requests.RequestNotFoundException;
import com.example.CanchaSystem.model.*;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.OwnerRepository;
import com.example.CanchaSystem.repository.OwnerRequestRepository;
import com.example.CanchaSystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerRequestService {

    @Autowired
    OwnerRequestRepository ownerRequestRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MailService mailService;

    public OwnerRequest insertRequest(OwnerRequest ownerRequest){

        if(ownerRequestRepository.existsByClientIdAndStatus(ownerRequest.getClient().getId(), OwnerRequestStatus.PENDING))
            throw new ClientAlreadyRequestedException("El usuario ya tiene una solicitud pendiente");

        Client sendTo = clientRepository.findByIdAndActive(ownerRequest.getClient().getId(),true)
                .orElseThrow(()->new ClientNotFoundException("El cliente no se encontro"));

        mailService.sendRequestNoticeToClient(sendTo.getMail(),ownerRequest);
        return ownerRequestRepository.save(ownerRequest);
    }

    public List<OwnerRequest> getAllRequests() {
        List<OwnerRequest> requests = ownerRequestRepository.findAll();

        return requests;
    }


    public List<OwnerRequest> getAllPendingRequests(){
        List<OwnerRequest> requests = ownerRequestRepository.findByStatusAndActive(OwnerRequestStatus.PENDING, true);
;
        return requests;
    }

    public ResponseEntity<String> updateRequest(Long id, OwnerRequestStatus status){
        OwnerRequest ownerRequest = ownerRequestRepository.findById(id)
                .orElseThrow(()->new RequestNotFoundException("No se encontro la solicitud"));

        Client sendTo = clientRepository.findByIdAndActive(ownerRequest.getClient().getId(),true)
                        .orElseThrow(()->new ClientNotFoundException("El cliente no se encontro"));

        ownerRequest.setStatus(status);
        if (ownerRequest.getStatus()==OwnerRequestStatus.APPROVED)
            mailService.sendRequestApprovedStatusUpdateToClient(sendTo.getMail(),ownerRequest);

        if (ownerRequest.getStatus()==OwnerRequestStatus.DENIED)
            mailService.sendRequestDeniedStatusUpdateToClient(sendTo.getMail(),ownerRequest);

        ownerRequestRepository.save(ownerRequest);
        return ResponseEntity.ok("Solicitud rechazada correctamente.");
    }

    @Scheduled(fixedRate = 60000)
    public void completeApprovedRequests() {
        List<OwnerRequest> approved = ownerRequestRepository.findByStatusAndActive(OwnerRequestStatus.APPROVED, true);

        for (OwnerRequest request : approved) {
            try {
                Client approvedClient = clientRepository.findById(request.getClient().getId())
                        .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

                if (ownerRepository.existsByUsernameAndActive(approvedClient.getUsername(), true)) {
                    request.setActive(false);
                    ownerRequestRepository.save(request);
                    continue;
                }

                Owner newOwner = new Owner();
                newOwner.setName(approvedClient.getName());
                newOwner.setLastName(approvedClient.getLastName());
                newOwner.setUsername(approvedClient.getUsername());
                newOwner.setPassword(approvedClient.getPassword());
                newOwner.setMail(approvedClient.getMail());
                newOwner.setCellNumber(approvedClient.getCellNumber());
                newOwner.setBankOwner(approvedClient.getBankClient());

                Role ownerRole = roleRepository.findByName("OWNER")
                        .orElseGet(() -> roleRepository.save(new Role("OWNER")));

                newOwner.setRole(ownerRole);
                newOwner.setActive(true);
                ownerRepository.save(newOwner);

                approvedClient.setActive(false);
                request.setActive(false);

                clientRepository.save(approvedClient);
                ownerRequestRepository.save(request);

            } catch (Exception e) {
                System.err.println("Error procesando solicitud con id " + request.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public OwnerRequest findByClientId(Long clientId){
        return ownerRequestRepository.findByClientId(clientId)
                .orElseThrow(()->new ClientNotFoundException("Cliente no encontrado"));
    }


}
