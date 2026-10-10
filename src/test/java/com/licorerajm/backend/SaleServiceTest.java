
package com.licorerajm.backend;

import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.entity.*;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.repository.*;
import com.licorerajm.backend.service.CurrentUserService;
import com.licorerajm.backend.service.SaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock SaleRepository saleRepository;
    @Mock SaleDetailRepository saleDetailRepository;
    @Mock SaleDetailLotRepository saleDetailLotRepository;
    @Mock SalePaymentRepository salePaymentRepository;
    @Mock PaymentMethodRepository paymentMethodRepository;
    @Mock ProductRepository productRepository;
    @Mock CashRegisterRepository cashRegisterRepository;
    @Mock CreditAccountRepository creditAccountRepository;
    @Mock CreditPaymentRepository creditPaymentRepository;
    @Mock UserRepository userRepository;
    @Mock InventoryLotRepository inventoryLotRepository;
    @Mock SaleNumberRepository saleNumberRepository;
    @Mock CurrentUserService currentUserService;

    @InjectMocks
    SaleService saleService;

    @Test
    void shouldRejectCancellationWhenCreditPaymentsExist() {
        User user = mock(User.class);

        when(currentUserService.getCurrentUser()).thenReturn(
                new CurrentUserResponse(
                        1L, "Usuario", "Prueba",
                        "usuario.prueba", "ADMINISTRADOR", true
                )
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Sale sale = mock(Sale.class);
        when(sale.getId()).thenReturn(20L);
        when(sale.getStatus()).thenReturn("COMPLETED");
        when(saleRepository.findWithLockById(20L))
                .thenReturn(Optional.of(sale));

        CreditAccount account = mock(CreditAccount.class);
        when(account.getId()).thenReturn(10L);
        when(creditAccountRepository.findBySaleId(20L))
                .thenReturn(Optional.of(account));
        when(creditAccountRepository.findWithLockById(10L))
                .thenReturn(Optional.of(account));

        CreditPayment payment = mock(CreditPayment.class);
        when(creditPaymentRepository
                .findByCreditAccountIdOrderByPaymentDateDescIdDesc(10L))
                .thenReturn(List.of(payment));

        assertThrows(
                DuplicateResourceException.class,
                () -> saleService.cancelSale(20L, null)
        );

        verify(sale, never()).setStatus("CANCELLED");
        verify(account, never()).setBalance(BigDecimal.ZERO);
        verify(creditAccountRepository, never()).save(account);

        // La cancelación se bloquea antes de modificar caja o inventario.
        verifyNoInteractions(cashRegisterRepository);
        verifyNoInteractions(saleDetailRepository);
        verifyNoInteractions(saleDetailLotRepository);
        verifyNoInteractions(inventoryLotRepository);
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldAllowCancellationWhenCreditAccountHasNoPayments() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("usuario.prueba");

        when(currentUserService.getCurrentUser()).thenReturn(
                new CurrentUserResponse(
                        1L, "Usuario", "Prueba",
                        "usuario.prueba", "ADMINISTRADOR", true
                )
        );
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        CashRegister originalRegister = mock(CashRegister.class);
        when(originalRegister.getId()).thenReturn(5L);

        Sale sale = mock(Sale.class);
        when(sale.getId()).thenReturn(20L);
        when(sale.getStatus()).thenReturn("COMPLETED");
        when(sale.getCashRegister()).thenReturn(originalRegister);
        when(sale.getUser()).thenReturn(user);
        when(sale.getSubtotal()).thenReturn(new BigDecimal("100000.00"));
        when(sale.getDiscount()).thenReturn(BigDecimal.ZERO);
        when(sale.getTotal()).thenReturn(new BigDecimal("100000.00"));

        when(saleRepository.findWithLockById(20L))
                .thenReturn(Optional.of(sale));
        when(saleRepository.save(sale)).thenReturn(sale);

        CreditAccount account = mock(CreditAccount.class);
        when(account.getId()).thenReturn(10L);
        when(account.getCustomerName()).thenReturn("Cliente de prueba");
        when(account.getBalance()).thenReturn(BigDecimal.ZERO);

        when(creditAccountRepository.findBySaleId(20L))
                .thenReturn(Optional.of(account));
        when(creditAccountRepository.findWithLockById(10L))
                .thenReturn(Optional.of(account));

        when(creditPaymentRepository
                .findByCreditAccountIdOrderByPaymentDateDescIdDesc(10L))
                .thenReturn(List.of());

        CashRegister register = mock(CashRegister.class);
        when(register.getOpeningAmount()).thenReturn(
                new BigDecimal("200000.00"));
        when(register.getCashSales()).thenReturn(BigDecimal.ZERO);
        when(register.getTransferSales()).thenReturn(BigDecimal.ZERO);
        when(register.getTotalSales()).thenReturn(BigDecimal.ZERO);
        when(register.getCashCollections()).thenReturn(BigDecimal.ZERO);
        when(register.getCountedCash()).thenReturn(null);

        when(cashRegisterRepository.findWithLockById(5L))
                .thenReturn(Optional.of(register));

        when(saleDetailRepository.findBySaleId(20L))
                .thenReturn(List.of());
        when(salePaymentRepository.findBySaleId(20L))
                .thenReturn(List.of());

        saleService.cancelSale(20L, null);

        verify(sale).setStatus("CANCELLED");
        verify(sale).setCancelledBy(user);
        verify(account).setStatus("CANCELLED");
        verify(account).setBalance(BigDecimal.ZERO);
        verify(creditAccountRepository).save(account);
        verify(cashRegisterRepository).save(register);
        verify(saleRepository).save(sale);
    }
}
