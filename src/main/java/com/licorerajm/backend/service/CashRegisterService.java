package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CashRegisterCloseRequest;
import com.licorerajm.backend.dto.CashRegisterResponse;
import com.licorerajm.backend.dto.CashRegisterRequest;
import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.entity.CashRegister;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.CashRegisterRepository;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    public CashRegisterService(
            CashRegisterRepository cashRegisterRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService) {

        this.cashRegisterRepository = cashRegisterRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public List<CashRegisterResponse> findAll() {
        return cashRegisterRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CashRegisterResponse findById(Long id) {
        CashRegister cashRegister = cashRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La caja no fue encontrada"));

        return toResponse(cashRegister);
    }

    public CashRegisterResponse openCashRegister(CashRegisterRequest request) {

        CurrentUserResponse currentUser = currentUserService.getCurrentUser();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        if (cashRegisterRepository.existsByUserIdAndStatus(
                user.getId(), "OPEN")) {

            throw new DuplicateResourceException(
                    "El usuario ya tiene una caja abierta");
        }

        CashRegister cashRegister = new CashRegister();

        cashRegister.setUser(user);
        cashRegister.setOpeningAmount(request.getOpeningAmount());
        cashRegister.setCashSales(BigDecimal.ZERO);
        cashRegister.setTransferSales(BigDecimal.ZERO);
        cashRegister.setTotalSales(BigDecimal.ZERO);
        cashRegister.setExpectedCash(request.getOpeningAmount());
        cashRegister.setStatus("OPEN");
        cashRegister.setNotes(request.getNotes());

        CashRegister savedCashRegister = cashRegisterRepository.save(cashRegister);

        return toResponse(savedCashRegister);
    }

    public CashRegisterResponse closeCashRegister(Long id, CashRegisterCloseRequest request) {

        CashRegister cashRegister = cashRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La caja no fue encontrada"));

        if (!"OPEN".equals(cashRegister.getStatus())) {
            throw new DuplicateResourceException("La caja ya está cerrada");
        }

        BigDecimal expectedCash = cashRegister.getOpeningAmount()
                .add(cashRegister.getCashSales());

        BigDecimal difference = request.getCountedCash()
                .subtract(expectedCash);

        cashRegister.setExpectedCash(expectedCash);
        cashRegister.setCountedCash(request.getCountedCash());
        cashRegister.setDifference(difference);
        cashRegister.setClosedAt(LocalDateTime.now());
        cashRegister.setStatus("CLOSED");

        if (request.getNotes() != null) {
            cashRegister.setNotes(request.getNotes());
        }

        CashRegister savedCashRegister = cashRegisterRepository.save(cashRegister);

        return toResponse(savedCashRegister);
    }

    private CashRegisterResponse toResponse(CashRegister cashRegister) {
        CashRegisterResponse response = new CashRegisterResponse();

        response.setId(cashRegister.getId());
        response.setUserId(cashRegister.getUser().getId());
        response.setUsername(cashRegister.getUser().getUsername());
        response.setOpenedAt(cashRegister.getOpenedAt());
        response.setClosedAt(cashRegister.getClosedAt());
        response.setOpeningAmount(cashRegister.getOpeningAmount());
        response.setCashSales(cashRegister.getCashSales());
        response.setTransferSales(cashRegister.getTransferSales());
        response.setTotalSales(cashRegister.getTotalSales());
        response.setExpectedCash(cashRegister.getExpectedCash());
        response.setCountedCash(cashRegister.getCountedCash());
        response.setDifference(cashRegister.getDifference());
        response.setStatus(cashRegister.getStatus());
        response.setNotes(cashRegister.getNotes());

        return response;
    }
}