package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.admin.AdminNotFoundException;
import com.example.CanchaSystem.exception.admin.NoAdminsException;
import com.example.CanchaSystem.exception.cancha.CanchaNameAlreadyExistsException;
import com.example.CanchaSystem.exception.cancha.CanchaNotFoundException;
import com.example.CanchaSystem.exception.cancha.IllegalCanchaAddressException;
import com.example.CanchaSystem.exception.cancha.NoCanchasException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNameAlreadyExistsException;
import com.example.CanchaSystem.exception.canchaBrand.CanchaBrandNotFoundException;
import com.example.CanchaSystem.exception.canchaBrand.NoCanchaBrandsException;
import com.example.CanchaSystem.exception.client.*;
import com.example.CanchaSystem.exception.misc.*;
import com.example.CanchaSystem.exception.owner.NoOwnersException;
import com.example.CanchaSystem.exception.owner.OwnerNotFoundException;
import com.example.CanchaSystem.exception.owner.UnactiveOwnerException;
import com.example.CanchaSystem.exception.requests.NoRequestsException;
import com.example.CanchaSystem.exception.requests.RequestNotFoundException;
import com.example.CanchaSystem.exception.reservation.IllegalReservationDateException;
import com.example.CanchaSystem.exception.reservation.NoReservationsException;
import com.example.CanchaSystem.exception.reservation.ReservationNotFoundException;
import com.example.CanchaSystem.exception.review.NoReviewsException;
import com.example.CanchaSystem.exception.review.ReviewNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAdminNotFound(AdminNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoAdminsException.class)
    public ResponseEntity<Map<String, Object>> handleNoAdmins(NoAdminsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(CanchaNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCanchaNameAlreadyExists(CanchaNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(CanchaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCanchaNotFound(CanchaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalCanchaAddressException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalCanchaAddress(IllegalCanchaAddressException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoCanchasException.class)
    public ResponseEntity<Map<String, Object>> handleNoCanchas(NoCanchasException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(CanchaBrandNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCanchaBrandNameAlreadyExists(CanchaBrandNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(CanchaBrandNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCanchaBrandNotFound(CanchaBrandNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoCanchaBrandsException.class)
    public ResponseEntity<Map<String, Object>> handleNoCanchaBrands(NoCanchaBrandsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClientNotFound(ClientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoClientsException.class)
    public ResponseEntity<Map<String, Object>> handleNoClients(NoClientsException ex) {
        //System.out.println("Entro al handler");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(UnactiveClientException.class)
    public ResponseEntity<Map<String, Object>> handleUnactiveClient(UnactiveClientException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(ClientAlreadyRequestedException.class)
    public ResponseEntity<Map<String, Object>> handleClientAlreadyRequested(ClientAlreadyRequestedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<Map<String, Object>> handleNotEnoughMoney(NotEnoughMoneyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }


    @ExceptionHandler(BankAlreadyLinkedException.class)
    public ResponseEntity<Map<String, Object>> handleBankAlreadyLinked(BankAlreadyLinkedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(CellNumberAlreadyAddedException.class)
    public ResponseEntity<Map<String, Object>> handleCellNumberAlreadyAdded(CellNumberAlreadyAddedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(MailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleMailAlreadyRegistered(MailAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(UnableToDropException.class)
    public ResponseEntity<Map<String, Object>> handleUnableToDrop(UnactiveClientException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoOwnersException.class)
    public ResponseEntity<Map<String, Object>> handleNoOwners(NoOwnersException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOwnerNotFound(OwnerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(UnactiveOwnerException.class)
    public ResponseEntity<Map<String, Object>> handleUnactiveOwner(UnactiveOwnerException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoRequestsException.class)
    public ResponseEntity<Map<String, Object>> handleNoRequests(NoRequestsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRequestNotFound(RequestNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalReservationDateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalReservationDate(IllegalReservationDateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoReservationsException.class)
    public ResponseEntity<Map<String, Object>> handleNoReservations(NoReservationsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReservationNotFound(ReservationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(NoReviewsException.class)
    public ResponseEntity<Map<String, Object>> handleNoReviews(NoReviewsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReviewNotFound(ReviewNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<Map<String, Object>> handleMissingData(MissingDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage(), "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error", "timestamp", LocalDateTime.now()));
    }
}
