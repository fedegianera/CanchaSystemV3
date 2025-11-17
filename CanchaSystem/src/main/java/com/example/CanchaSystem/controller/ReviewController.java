package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ClientRepository clientRepository;
    @PostMapping("/insert")
    public ResponseEntity<?> insertReview(@RequestBody @Valid ReviewRequestDTO dto) {
        ReviewResponseDTO saved = reviewService.insertReview(dto);
        return ResponseEntity.ok(saved);
    }


    @GetMapping("/findall")
    public ResponseEntity<?> getReviews() {
            return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewRequestDTO reviewDto) {
        ReviewResponseDTO review = reviewService.updateReview(id, reviewDto);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
            reviewService.deleteReview(id);
            return ResponseEntity.ok(Map.of("message","Reseña eliminada"));
    }

    @GetMapping("/findReviewById/{id}")
    public ResponseEntity<?> findReviewById(@PathVariable Long id) {
            return ResponseEntity.ok(reviewService.findReviewById(id));
    }

    @GetMapping("/findReviewsByClientId/{id}")
    public ResponseEntity<?> findReviewsByClientId(UUID id) {
        return ResponseEntity.ok(reviewService.getAllReviewsByClientId(id));
    }

    @GetMapping("/findReviewsByEstablishmentId/{establishmentId}")
    public ResponseEntity<?> findReviewsByEstablishmentId(@PathVariable Long establishmentId) {
        return ResponseEntity.ok(reviewService.getAllReviewsByEstablishmentId(establishmentId));
    }

    @GetMapping("/findReviewsByCanchaIdAdmin/{establishmentId}")
    public ResponseEntity<?> findReviewsByCanchaIdAdmin(@PathVariable Long canchaId){
        return ResponseEntity.ok(reviewService.getAllReviewsByCanchaIdAdmin(canchaId));
    }

    @GetMapping("/clientReviewExists/{establishmentId}/{clientId}")
    public boolean clientAlreadyReviewedCancha(@PathVariable Long establishmentId , @PathVariable UUID clientId){
        return reviewService.clientAlreadyReviewedCancha(establishmentId ,clientId);
    }



}
