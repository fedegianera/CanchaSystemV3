package com.example.CanchaSystem.service;

import com.example.CanchaSystem.Mapper.ReviewMapper;
import com.example.CanchaSystem.dto.EstablishmentRatingDTO;
import com.example.CanchaSystem.dto.request.ReviewRequestDTO;
import com.example.CanchaSystem.dto.response.CanchaResponseDTO;
import com.example.CanchaSystem.dto.response.ReviewResponseDTO;
import com.example.CanchaSystem.exception.client.ClientNotFoundException;
import com.example.CanchaSystem.exception.misc.UnableToDropException;
import com.example.CanchaSystem.exception.review.NoReviewsException;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import com.example.CanchaSystem.model.Client;
import com.example.CanchaSystem.model.Establishment;
import com.example.CanchaSystem.model.Review;
import com.example.CanchaSystem.repository.ClientRepository;
import com.example.CanchaSystem.repository.EstablishmentRepository;
import com.example.CanchaSystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private EstablishmentRepository establishmentRepository;

    public ReviewResponseDTO insertReview(ReviewRequestDTO dto) {
        // 1️⃣ Buscar el cliente por su UUID
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // 2️⃣ Buscar el establecimiento
        Establishment est = establishmentRepository.findById(dto.establishmentId())
                .orElseThrow(() -> new RuntimeException("Establishment not found"));

        // 3️⃣ Crear la review
        Review review = new Review();
        review.setRating(dto.rating());
        review.setMessage(dto.message());
        review.setClient(client);
        review.setClientName(dto.clientName());
        review.setCreatedAt(dto.createdAt());
        review.setEstablishment(est);
        review.setActive(true);

        System.out.println(review.getClient().getId());
        System.out.println(review.getClient().getId());
        System.out.println(review.getClient().getId());
        System.out.println(review.getClient().getId());
        System.out.println(review.getClient().getId());
        System.out.println(review.getClient().getId());


        // 4️⃣ Guardar
        reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }
    public List<ReviewResponseDTO> getAllReviews() throws NoReviewsException {
        List<Review> reviews = reviewRepository.findAll();

        if(reviews.isEmpty()){
            throw new NoReviewsException("Todavia no hay reseñas hechas");
        }

        return reviewMapper.toDto(reviews);
    }

    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO reviewDto) throws ReviewNotFoundException {
        Optional<Review> reviewOpt = reviewRepository.findByIdAndActive(id, true);

        if (reviewOpt.isEmpty()) {
            throw new ReviewNotFoundException("No se encontro ninguna review con ese id");
        }

        Review review = reviewOpt.get();

        review.setRating(reviewDto.rating());
        review.setMessage(reviewDto.message());

        reviewRepository.save(review);

        return reviewMapper.toDto(review);
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

        System.out.println("REVIEWS ---------------------------------------------");
        System.out.println(reviewMapper.toDto(reviews));

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

    public boolean clientAlreadyReviewedCancha(Long establishmentId ,UUID clientId){
        return reviewRepository.existsByEstablishmentIdAndClientIdAndActive(establishmentId , clientId, true);
    }

    public Double getEstablishmentAverageRating(Long id){
        return reviewRepository.getAverageRatingByEstablishment(id);
    }

    public List<EstablishmentRatingDTO> getAllEstablishmentAverageRatings() {
        List<Object[]> rows = reviewRepository.getAllEstablishmentAverages();

        return rows.stream()
                .map(r -> new EstablishmentRatingDTO(
                        (Long) r[0],
                        r[1] != null ? ((Double) r[1]) : 0.0
                ))
                .toList();
    }

}
