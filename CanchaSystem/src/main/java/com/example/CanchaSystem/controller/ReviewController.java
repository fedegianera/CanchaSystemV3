package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ClientRepository clientRepository;
    @PostMapping("/insert")
    public ResponseEntity<?> insertReview(
            @Validated @RequestBody Review review,
            @AuthenticationPrincipal UserDetails userDetails) {

        Client client = clientRepository.findByUsernameAndActive(userDetails.getUsername(), true)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));

        review.setClient(client);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.insertReview(review));
    }

    @GetMapping("/findall")
    public ResponseEntity<?> getReviews() {
            return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReview(@RequestBody Review review) {
            return ResponseEntity.ok(reviewService.updateReview(review));
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

    @GetMapping("/findReviewsByClient")
    public ResponseEntity<?> findReviewsByClientId(Authentication auth) {
        String username = auth.getName();

        return ResponseEntity.ok(reviewService.getAllReviewsByClient(username));
    }

    @GetMapping("/findReviewsByCanchaId/{canchaId}")
    public ResponseEntity<?> findReviewsByCanchaId(@PathVariable Long canchaId){
        return ResponseEntity.ok(reviewService.getAllReviewsByCanchaId(canchaId));
    }

    @GetMapping("/findReviewsByCanchaIdAdmin/{canchaId}")
    public ResponseEntity<?> findReviewsByCanchaIdAdmin(@PathVariable Long canchaId){
        return ResponseEntity.ok(reviewService.getAllReviewsByCanchaIdAdmin(canchaId));
    }

    @GetMapping("/clientReviewExists")
    public boolean clientAlreadyReviewedCancha(@RequestParam Long canchaId, @RequestParam Long clientId){
        return reviewService.clientAlreadyReviewedCancha(canchaId,clientId);
    }



}
