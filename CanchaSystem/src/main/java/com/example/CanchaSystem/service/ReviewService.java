package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ReviewMapper;
import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EstablishmentService establishmentService;

    public ReviewResponseDTO insertReview(ReviewRequestDTO dto) {
        // 1️⃣ Buscar el cliente por su UUID
        clientService.findClientOrThrow(dto.clientId());

        // 2️⃣ Buscar el establecimiento
        establishmentService.findEstablishmentOrThrow(dto.establishmentId());

        // 3️⃣ Crear la review
        Review review = reviewMapper.toEntity(dto);
        review.setActive(true);

        // 4️⃣ Guardar
        reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }

    public Review findReviewOrThrow(Long id) {
        return reviewRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public List<ReviewResponseDTO> getAllReviews() {
        return reviewMapper.toDto(
                reviewRepository.findAll()
        );
    }

    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO reviewDto) throws ReviewNotFoundException {
        Review review = findReviewOrThrow(id);

        review.setRating(reviewDto.rating());
        review.setMessage(reviewDto.message());

        reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }

    public void deleteReview(Long reviewId){
        Review review = findReviewOrThrow(reviewId);

        review.setActive(false);
        reviewRepository.save(review);
    }

    public ReviewResponseDTO findReviewById(Long id) throws ReviewNotFoundException {
        return reviewMapper.toDto(
                findReviewOrThrow(id)
        );
    }

    public List<ReviewResponseDTO> getAllReviewsByEstablishmentId(Long establishmentId) {
        List<Review> reviews = reviewRepository.findByEstablishmentIdAndActive(establishmentId, true);
        System.out.println("REVIEWS ---------------------------------------------");
        System.out.println(reviewMapper.toDto(reviews));

        return reviewMapper.toDto(reviews);
    }

    public List<ReviewResponseDTO> getAllReviewsByCanchaIdAdmin(Long canchaId) {
        List<Review> reviews = reviewRepository.findByEstablishmentId(canchaId);
        return reviewMapper.toDto(reviews);
    }

    public boolean clientAlreadyReviewedCancha(Long establishmentId, UUID clientId){
        return reviewRepository.existsByEstablishmentIdAndClientIdAndActive(establishmentId, clientId, true);
    }
}
