package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.service.ClientService;
import com.example.CanchaSystem.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ClientService clientService;

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
    public ResponseEntity<?> findReviewsByClientId(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getAllReviewsByClientId(id));
    }

    @GetMapping("/findReviewsByEstablishmentId/{establishmentId}")
    public ResponseEntity<?> findReviewsByEstablishmentId(@PathVariable Long establishmentId) {
        return ResponseEntity.ok(reviewService.getAllReviewsByEstablishmentId(establishmentId));
    }

    @GetMapping("/findReviewsByCanchaIdAdmin/{establishmentId}")
    public ResponseEntity<?> findReviewsByCanchaIdAdmin(@PathVariable Long establishmentId){
        return ResponseEntity.ok(reviewService.getAllReviewsByCanchaIdAdmin(establishmentId));
    }

    @GetMapping("/clientReviewExists/{establishmentId}/{clientId}")
    public boolean clientAlreadyReviewedCancha(@PathVariable Long establishmentId , @PathVariable UUID clientId){
        return reviewService.clientAlreadyReviewedCancha(establishmentId ,clientId);
    }



}
