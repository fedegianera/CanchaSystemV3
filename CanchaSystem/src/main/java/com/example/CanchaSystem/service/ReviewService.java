package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ReviewMapper;
import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.review.NoReviewsException;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    public Review insertReview(Review review) {
                        return reviewRepository.save(review);
    }

    public List<ReviewResponseDTO> getAllReviews() throws NoReviewsException {
        List<Review> reviews = reviewRepository.findAll();

        if(reviews.isEmpty()){
            throw new NoReviewsException("Todavia no hay reseñas hechas");
        }

        return reviewMapper.toDto(reviews);
    }

    public Review updateReview(Long id, ReviewRequestDTO reviewDto) throws ReviewNotFoundException {
        Optional<Review> reviewOpt = reviewRepository.findByIdAndActive(id, true);

        if (reviewOpt.isEmpty()) {
            throw new ReviewNotFoundException("No se encontro ninguna review con ese id");
        }

        Review review = reviewOpt.get();

        review.setRating(reviewDto.rating());
        review.setMessage(reviewDto.message());

        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId){

        Review review = reviewRepository.findByIdAndActive(reviewId, true)
                .orElseThrow(() -> new ReviewNotFoundException("Review no encontrado"));

        if (!review.isActive())
            throw new UnableToDropException("La review ya esta inactiva");

        review.setActive(false);
        reviewRepository.save(review);

    }

    public ReviewResponseDTO findReviewById(Long id) throws ReviewNotFoundException {
        Optional<Review> reviewOpt = reviewRepository.findByIdAndActive(id, true);

        if (reviewOpt.isEmpty()) {
            throw new ReviewNotFoundException("No se encontro ninguna review con ese id");
        }

        Review review = reviewOpt.get();

        return reviewMapper.toDto(review);
    }

    public List<ReviewResponseDTO> getAllReviewsByEstablishmentId(Long establishmentId) throws NoReviewsException {
        List<Review> reviews = reviewRepository.findByEstablishmentIdAndActive(establishmentId, true);

        if (reviews.isEmpty()) {
            throw new NoReviewsException("La cancha aun no tiene reviews");

        }

        return reviewMapper.toDto(reviews);
    }

    public List<ReviewResponseDTO> getAllReviewsByCanchaIdAdmin(Long canchaId) throws NoReviewsException {
        List<Review> reviews = reviewRepository.findByEstablishmentId(canchaId);

        if (reviews.isEmpty()) {
            throw new NoReviewsException("La cancha aun no tiene reviews");

        }

        return reviewMapper.toDto(reviews);
    }

    public List<ReviewResponseDTO> getAllReviewsByClientId(UUID id) throws NoReviewsException, ClientNotFoundException {
        Optional<Client> clientOpt = clientRepository.findByIdAndActive(id, true);

        if (clientOpt.isEmpty()) {
            throw new ClientNotFoundException("Cliente no encontrado");
        }

        List<Review> reviews = reviewRepository.findByClientIdAndActive(id, true);

        if (reviews.isEmpty()){
            throw new NoReviewsException("Todavia no hay reseñas hechas por el cliente");
        }

        return reviewMapper.toDto(reviews);
    }

    public boolean clientAlreadyReviewedCancha(Long canchaId,Long clientId){
        return reviewRepository.existsByEstablishmentIdAndClientIdAndActive(canchaId,clientId, true);
    }

}
