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
        List<Review> reviews = reviewRepository.findAll();
        if(!reviews.isEmpty()){
            return reviews;
        }else
            throw new NoReviewsException("Todavia no hay reseñas hechas");
    }

    public Review updateReview(Review review) throws ReviewNotFoundException {
        Review existing = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada"));

        existing.setRating(review.getRating());
        existing.setMessage(review.getMessage());
        return reviewRepository.save(existing);
    }

    public void deleteReview(Long reviewId){

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review no encontrado"));

        if (!review.isActive())
            throw new UnableToDropException("La review ya esta inactiva");

        review.setActive(false);
        reviewRepository.save(review);

    }

    public Review findReviewById(Long id) throws ReviewNotFoundException {
        return reviewRepository.findById(id).orElseThrow(()-> new ReviewNotFoundException("Reseña no encontrada"));
    }

    public List<Review> getAllReviewsByCanchaId(Long canchaId) throws NoReviewsException {
        List<Review> reviews = reviewRepository.findByCanchaIdAndActive(canchaId, true);

        return reviews;
    }

    public List<Review> getAllReviewsByCanchaIdAdmin(Long canchaId) throws NoReviewsException {
        List<Review> reviews = reviewRepository.findByCanchaId(canchaId);

        return reviews;
    }

    public List<Review> getAllReviewsByClient(String username) throws NoReviewsException, ClientNotFoundException {
        Optional<Client> clientOpt = clientRepository.findByUsernameAndActive(username, true);

        if (clientOpt.isEmpty()) {
            throw new ClientNotFoundException("Cliente no encontrado");
        }

        Client client = clientOpt.get();

        List<Review> reviews = reviewRepository.findByClientIdAndActive(client.getId(), true);

        if (!reviews.isEmpty()){
            return reviews;
        }else
            throw new NoReviewsException("Todavia no hay reseñas hechas por el cliente");
    }

    public boolean clientAlreadyReviewedCancha(Long canchaId,Long clientId){
        return reviewRepository.existsByCanchaIdAndClientIdAndActive(canchaId,clientId, true);
    }

}
