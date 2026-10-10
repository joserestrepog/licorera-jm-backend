package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CreditAccountResponse;
import com.licorerajm.backend.dto.CreditPaymentRequest;
import com.licorerajm.backend.dto.CreditPaymentResponse;
import com.licorerajm.backend.entity.CashRegister;
import com.licorerajm.backend.entity.CreditAccount;
import com.licorerajm.backend.entity.CreditPayment;
import com.licorerajm.backend.entity.PaymentMethod;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.CashRegisterRepository;
import com.licorerajm.backend.repository.CreditAccountRepository;
import com.licorerajm.backend.repository.CreditPaymentRepository;
import com.licorerajm.backend.repository.PaymentMethodRepository;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CreditService {

    private final CreditAccountRepository creditAccountRepository;
    private final CreditPaymentRepository creditPaymentRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public CreditService(
            CreditAccountRepository creditAccountRepository,
            CreditPaymentRepository creditPaymentRepository,
            CashRegisterRepository cashRegisterRepository,
            PaymentMethodRepository paymentMethodRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.creditAccountRepository = creditAccountRepository;
        this.creditPaymentRepository = creditPaymentRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<CreditAccountResponse> findOutstandingCredits() {
        List<CreditAccount> accounts = new ArrayList<>();

        accounts.addAll(
                creditAccountRepository.findByStatusOrderByCreatedAtDesc("PENDING")
        );

        accounts.addAll(
                creditAccountRepository.findByStatusOrderByCreatedAtDesc("PARTIAL")
        );

        return accounts.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditAccountResponse findById(Long id) {
        CreditAccount account = creditAccountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La cuenta por cobrar no fue encontrada"
                        )
                );

        return toResponse(account);
    }

    @Transactional
    public CreditAccountResponse registerPayment(
            Long creditAccountId,
            CreditPaymentRequest request
    ) {
        var currentUser = currentUserService.getCurrentUser();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado")
                );

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new DuplicateResourceException(
                    "El usuario está inactivo"
            );
        }

        CreditAccount account =
                creditAccountRepository.findWithLockById(creditAccountId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "La cuenta por cobrar no fue encontrada"
                                )
                        );

        if (!"PENDING".equals(account.getStatus())
                && !"PARTIAL".equals(account.getStatus())) {
            throw new DuplicateResourceException(
                    "La cuenta no tiene saldo pendiente"
            );
        }

        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DuplicateResourceException(
                    "El monto del abono debe ser mayor que cero"
            );
        }

        if (request.getAmount().compareTo(account.getBalance()) > 0) {
            throw new DuplicateResourceException(
                    "El abono no puede superar el saldo pendiente"
            );
        }

        CashRegister cashRegister =
                cashRegisterRepository.findWithLockById(
                        cashRegisterRepository
                                .findByUserIdAndStatus(user.getId(), "OPEN")
                                .orElseThrow(() ->
                                        new DuplicateResourceException(
                                                "Debes tener una caja abierta para registrar abonos"
                                        )
                                )
                                .getId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException("La caja no fue encontrada")
                );

        PaymentMethod paymentMethod =
                paymentMethodRepository.findById(request.getPaymentMethodId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "El método de pago no fue encontrado"
                                )
                        );

        if (!Boolean.TRUE.equals(paymentMethod.getActive())) {
            throw new DuplicateResourceException(
                    "El método de pago está inactivo"
            );
        }

        String methodName = paymentMethod.getName()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!"EFECTIVO".equals(methodName)
                && !"TRANSFERENCIA".equals(methodName)) {
            throw new DuplicateResourceException(
                    "Los abonos solo pueden recibirse en efectivo o transferencia"
            );
        }

        CreditPayment payment = new CreditPayment();
        payment.setCreditAccount(account);
        payment.setCashRegister(cashRegister);
        payment.setUser(user);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(request.getAmount());

        creditPaymentRepository.save(payment);

        BigDecimal newBalance =
                account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);
        account.setStatus(
                newBalance.compareTo(BigDecimal.ZERO) == 0
                        ? "PAID"
                        : "PARTIAL"
        );

        creditAccountRepository.save(account);

        if ("EFECTIVO".equals(methodName)) {
            cashRegister.setCashCollections(
                    cashRegister.getCashCollections()
                            .add(request.getAmount())
            );

            cashRegister.setExpectedCash(
                    cashRegister.getOpeningAmount()
                            .add(cashRegister.getCashSales())
                            .add(cashRegister.getCashCollections())
            );

            cashRegisterRepository.save(cashRegister);
        }

        return toResponse(account);
    }

    private CreditAccountResponse toResponse(CreditAccount account) {
        CreditAccountResponse response = new CreditAccountResponse();

        response.setId(account.getId());
        response.setSaleId(account.getSale().getId());
        response.setSaleNumber(
                String.valueOf(account.getSale().getSaleNumber())
        );
        response.setCustomerName(account.getCustomerName());
        response.setInitialAmount(account.getInitialAmount());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        List<CreditPaymentResponse> payments =
                creditPaymentRepository
                        .findByCreditAccountIdOrderByPaymentDateDescIdDesc(
                                account.getId()
                        )
                        .stream()
                        .map(this::toPaymentResponse)
                        .toList();

        response.setPayments(payments);

        return response;
    }

    private CreditPaymentResponse toPaymentResponse(CreditPayment payment) {
        CreditPaymentResponse response = new CreditPaymentResponse();

        response.setId(payment.getId());
        response.setCreditAccountId(payment.getCreditAccount().getId());
        response.setCashRegisterId(payment.getCashRegister().getId());
        response.setUserId(payment.getUser().getId());
        response.setUsername(payment.getUser().getUsername());
        response.setPaymentMethodId(payment.getPaymentMethod().getId());
        response.setPaymentMethodName(payment.getPaymentMethod().getName());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());

        return response;
    }
}