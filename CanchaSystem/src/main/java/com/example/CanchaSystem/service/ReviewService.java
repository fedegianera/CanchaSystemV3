package com.example.CanchaSystem.service;

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

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Review insertReview(Review review) {
                        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() throws NoReviewsException {
        return reviewRepository.findAll();
    }

    public Review updateReview(Review review) throws ReviewNotFoundException {
        Review existing = findReviewById(review.getId());

        existing.setRating(review.getRating());
        existing.setMessage(review.getMessage());

        return reviewRepository.save(existing);
    }

    public void deleteReview(Long reviewId){
        Review review = findReviewById(reviewId);

        if (!review.isActive())
            throw new UnableToDropException("La review ya está inactiva");

        review.setActive(false);
        reviewRepository.save(review);
    }

    public Review findReviewById(Long id) throws ReviewNotFoundException {
        return reviewRepository.findById(id)
                .orElseThrow(()-> new ReviewNotFoundException("Reseña no encontrada"));
    }

    public List<Review> getAllReviewsByCanchaId(Long canchaId) throws NoReviewsException {
        return reviewRepository.findByCanchaIdAndActive(canchaId, true);
    }

    public List<Review> getAllReviewsByCanchaIdAdmin(Long canchaId) throws NoReviewsException {
        return reviewRepository.findByCanchaId(canchaId);
    }

    public List<Review> getAllReviewsByClient(String username) throws NoReviewsException, ClientNotFoundException {
        Optional<Client> clientOpt = clientRepository.findByUsernameAndActive(username, true);

        if (clientOpt.isEmpty()) {
            throw new ClientNotFoundException();
        }

        Client client = clientOpt.get();

        return reviewRepository.findByClientIdAndActive(client.getId(), true);
    }

    public boolean clientAlreadyReviewedCancha(Long canchaId,Long clientId){
        return reviewRepository.existsByCanchaIdAndClientIdAndActive(canchaId,clientId, true);
    }
}
