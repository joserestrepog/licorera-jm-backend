
package com.licorerajm.backend;

import com.licorerajm.backend.entity.CashRegister;
import com.licorerajm.backend.dto.CreditPaymentRequest;
import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.entity.CreditAccount;
import com.licorerajm.backend.entity.PaymentMethod;
import com.licorerajm.backend.entity.Sale;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.repository.CashRegisterRepository;
import com.licorerajm.backend.repository.CreditAccountRepository;
import com.licorerajm.backend.repository.CreditPaymentRepository;
import com.licorerajm.backend.repository.PaymentMethodRepository;
import com.licorerajm.backend.repository.UserRepository;
import com.licorerajm.backend.service.CreditService;
import com.licorerajm.backend.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreditServiceTest {

    private CreditAccountRepository creditAccountRepository;
    private CreditPaymentRepository creditPaymentRepository;
    private CashRegisterRepository cashRegisterRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private UserRepository userRepository;
    private CurrentUserService currentUserService;

    private CreditService creditService;

    @BeforeEach
    void setUp() {
        creditAccountRepository = mock(CreditAccountRepository.class);
        creditPaymentRepository = mock(CreditPaymentRepository.class);
        cashRegisterRepository = mock(CashRegisterRepository.class);
        paymentMethodRepository = mock(PaymentMethodRepository.class);
        userRepository = mock(UserRepository.class);
        currentUserService = mock(CurrentUserService.class);

        creditService = new CreditService(
                creditAccountRepository,
                creditPaymentRepository,
                cashRegisterRepository,
                paymentMethodRepository,
                userRepository,
                currentUserService
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(new CurrentUserResponse(
                        1L,
                        "Usuario",
                        "Prueba",
                        "usuario.prueba",
                        "ADMINISTRADOR",
                        true
                ));
    }

    @Test
    void shouldRejectPaymentGreaterThanOutstandingBalance() {
        User user = mock(User.class);
        when(user.getActive()).thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        CreditAccount account = mock(CreditAccount.class);
        when(account.getStatus()).thenReturn("PENDING");
        when(account.getBalance()).thenReturn(new BigDecimal("100000.00"));

        when(creditAccountRepository.findWithLockById(10L))
                .thenReturn(Optional.of(account));

        CreditPaymentRequest request = new CreditPaymentRequest();
        request.setAmount(new BigDecimal("100001.00"));
        request.setPaymentMethodId(1L);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> creditService.registerPayment(10L, request)
        );

        assertEquals(
                "El abono no puede superar el saldo pendiente",
                exception.getMessage()
        );

        verify(creditPaymentRepository, never()).save(any());
        verify(creditAccountRepository, never()).save(any());
        verifyNoInteractions(cashRegisterRepository);
        verifyNoInteractions(paymentMethodRepository);
    }

    @Test
    void shouldRejectZeroPayment() {
        User user = mock(User.class);
        when(user.getActive()).thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        CreditAccount account = mock(CreditAccount.class);
        when(account.getStatus()).thenReturn("PENDING");
        when(account.getBalance()).thenReturn(new BigDecimal("100000.00"));

        when(creditAccountRepository.findWithLockById(10L))
                .thenReturn(Optional.of(account));

        CreditPaymentRequest request = new CreditPaymentRequest();
        request.setAmount(BigDecimal.ZERO);
        request.setPaymentMethodId(1L);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> creditService.registerPayment(10L, request)
        );

        assertEquals(
                "El monto del abono debe ser mayor que cero",
                exception.getMessage()
        );

        verify(creditPaymentRepository, never()).save(any());
        verify(creditAccountRepository, never()).save(any());
        verifyNoInteractions(cashRegisterRepository);
        verifyNoInteractions(paymentMethodRepository);
    }


    @Test
    void shouldRegisterCashPaymentAndUpdateExpectedCash() {
        CashRegister cashRegister = prepareValidPayment("EFECTIVO");

        CreditPaymentRequest request = new CreditPaymentRequest();
        request.setAmount(new BigDecimal("40000.00"));
        request.setPaymentMethodId(1L);

        creditService.registerPayment(10L, request);

        verify(creditPaymentRepository).save(any());

        verify(creditAccountRepository).save(any());
        verify(creditAccountRepository).findWithLockById(10L);

        verify(cashRegister).setCashCollections(
                new BigDecimal("50000.00")
        );

        verify(cashRegister).setExpectedCash(
                new BigDecimal("250000.00")
        );

        verify(cashRegisterRepository).save(cashRegister);
    }

    @Test
    void shouldRegisterTransferWithoutIncreasingExpectedCash() {
        CashRegister cashRegister = prepareValidPayment("TRANSFERENCIA");

        CreditPaymentRequest request = new CreditPaymentRequest();
        request.setAmount(new BigDecimal("40000.00"));
        request.setPaymentMethodId(1L);

        creditService.registerPayment(10L, request);

        verify(creditPaymentRepository).save(any());
        verify(creditAccountRepository).save(any());

        verify(cashRegisterRepository, never()).save(any());

        verify(cashRegister, never()).setCashCollections(any());
        verify(cashRegister, never()).setExpectedCash(any());
    }

    private CashRegister prepareValidPayment(String methodName) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getActive()).thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Sale sale = mock(Sale.class);
        when(sale.getId()).thenReturn(20L);
        when(sale.getSaleNumber()).thenReturn(20L);

        CreditAccount account = mock(CreditAccount.class);
        when(account.getId()).thenReturn(10L);
        when(account.getSale()).thenReturn(sale);
        when(account.getStatus()).thenReturn("PENDING");
        when(account.getCustomerName()).thenReturn("Cliente de prueba");
        when(account.getInitialAmount()).thenReturn(
                new BigDecimal("100000.00")
        );
        when(account.getBalance()).thenReturn(
                new BigDecimal("100000.00")
        );
        when(creditAccountRepository.findWithLockById(10L))
                .thenReturn(Optional.of(account));

        CashRegister cashRegister = mock(CashRegister.class);
        when(cashRegister.getId()).thenReturn(5L);
        when(cashRegister.getOpeningAmount())
                .thenReturn(new BigDecimal("200000.00"));
        when(cashRegister.getCashSales())
                .thenReturn(new BigDecimal("0.00"));
        when(cashRegister.getCashCollections())
                .thenReturn(
                        new BigDecimal("10000.00"),
                        new BigDecimal("50000.00")
                );

        when(cashRegisterRepository.findByUserIdAndStatus(1L, "OPEN"))
                .thenReturn(Optional.of(cashRegister));
        when(cashRegisterRepository.findWithLockById(5L))
                .thenReturn(Optional.of(cashRegister));

        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        when(paymentMethod.getActive()).thenReturn(true);
        when(paymentMethod.getName()).thenReturn(methodName);
        when(paymentMethodRepository.findById(1L))
                .thenReturn(Optional.of(paymentMethod));

        when(creditPaymentRepository
                .findByCreditAccountIdOrderByPaymentDateDescIdDesc(10L))
                .thenReturn(List.of());

        return cashRegister;
    }

}
