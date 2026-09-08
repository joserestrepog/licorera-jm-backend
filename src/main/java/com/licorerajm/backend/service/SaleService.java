package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.dto.SaleCancelRequest;
import com.licorerajm.backend.dto.SaleItemRequest;
import com.licorerajm.backend.dto.SaleRequest;
import com.licorerajm.backend.dto.SaleResponse;
import com.licorerajm.backend.entity.*;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.CashRegisterRepository;
import com.licorerajm.backend.repository.InventoryLotRepository;
import com.licorerajm.backend.repository.PaymentMethodRepository;
import com.licorerajm.backend.repository.ProductRepository;
import com.licorerajm.backend.repository.SaleDetailLotRepository;
import com.licorerajm.backend.repository.SaleDetailRepository;
import com.licorerajm.backend.repository.SaleNumberRepository;
import com.licorerajm.backend.repository.SalePaymentRepository;
import com.licorerajm.backend.repository.SaleRepository;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final SaleDetailLotRepository saleDetailLotRepository;
    private final SalePaymentRepository salePaymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final UserRepository userRepository;
    private final InventoryLotRepository inventoryLotRepository;

    private final CurrentUserService currentUserService;
    private final SaleNumberRepository saleNumberRepository;

    public SaleService(
            SaleRepository saleRepository,
            SaleDetailRepository saleDetailRepository,
            SaleDetailLotRepository saleDetailLotRepository,
            SalePaymentRepository salePaymentRepository,
            PaymentMethodRepository paymentMethodRepository,
            ProductRepository productRepository,
            CashRegisterRepository cashRegisterRepository,
            UserRepository userRepository,
            InventoryLotRepository inventoryLotRepository,
            SaleNumberRepository saleNumberRepository,
            CurrentUserService currentUserService
    ) {
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.saleDetailLotRepository = saleDetailLotRepository;
        this.salePaymentRepository = salePaymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.productRepository = productRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.userRepository = userRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.saleNumberRepository = saleNumberRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public SaleResponse createSale(SaleRequest request) {

        CurrentUserResponse currentUser = currentUserService.getCurrentUser();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        CashRegister cashRegister = cashRegisterRepository.findWithLockById(request.getCashRegisterId())
                .orElseThrow(() -> new ResourceNotFoundException("La caja no fue encontrada"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new DuplicateResourceException("El usuario está inactivo");
        }

        if (!"OPEN".equals(cashRegister.getStatus())) {
            throw new DuplicateResourceException("La caja no está abierta");
        }

        if (!cashRegister.getUser().getId().equals(user.getId())) {
            throw new DuplicateResourceException(
                    "La caja no pertenece al usuario indicado"
            );
        }

        Long saleNumber = saleNumberRepository.getNextSaleNumber();

        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        sale.setUser(user);
        sale.setCashRegister(cashRegister);
        sale.setStatus("PENDING");

        sale.setSubtotal(BigDecimal.ZERO);
        sale.setDiscount(request.getDiscount());
        sale.setTotal(BigDecimal.ZERO);

        Sale savedSale = saleRepository.save(sale);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal itemDiscountTotal = BigDecimal.ZERO;

        for (SaleItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findWithLockById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("El producto no fue encontrado"));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new DuplicateResourceException(
                        "El producto está inactivo: " + product.getName()
                );
            }

            if (product.getCurrentStock() < itemRequest.getQuantity()) {
                throw new DuplicateResourceException(
                        "Stock insuficiente para el producto: " + product.getName()
                );
            }

            BigDecimal unitPrice = product.getSalePrice();

            BigDecimal itemSubtotal = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            BigDecimal itemDiscount = itemRequest.getDiscount();

            if (itemDiscount.compareTo(itemSubtotal) > 0) {
                throw new DuplicateResourceException(
                        "El descuento no puede superar el subtotal del producto: "
                                + product.getName()
                );
            }

            BigDecimal itemTotal = itemSubtotal.subtract(itemDiscount);

            SaleDetail detail = new SaleDetail();

            detail.setSale(savedSale);
            detail.setProduct(product);
            detail.setQuantity(itemRequest.getQuantity());
            detail.setUnitPrice(unitPrice);
            detail.setDiscount(itemDiscount);
            detail.setSubtotal(itemSubtotal);
            detail.setTotal(itemTotal);

            saleDetailRepository.save(detail);

            BigDecimal totalCost = consumeFifo(
                    detail,
                    product,
                    itemRequest.getQuantity()
            );

            BigDecimal unitCost = totalCost.divide(
                    BigDecimal.valueOf(itemRequest.getQuantity()),
                    2,
                    RoundingMode.HALF_UP
            );

            BigDecimal profit = itemTotal.subtract(totalCost);

            detail.setUnitCost(unitCost);
            detail.setTotalCost(totalCost);
            detail.setProfit(profit);

            saleDetailRepository.save(detail);

            product.setCurrentStock(
                    product.getCurrentStock() - itemRequest.getQuantity()
            );

            productRepository.save(product);

            subtotal = subtotal.add(itemSubtotal);
            itemDiscountTotal = itemDiscountTotal.add(itemDiscount);
        }

        BigDecimal saleDiscount = request.getDiscount();

        BigDecimal totalDiscount = itemDiscountTotal.add(saleDiscount);

        if (totalDiscount.compareTo(subtotal) > 0) {
            throw new DuplicateResourceException(
                    "El descuento total no puede superar el subtotal de la venta"
            );
        }

        BigDecimal total = subtotal.subtract(totalDiscount);

        savedSale.setSubtotal(subtotal);
        savedSale.setDiscount(totalDiscount);
        savedSale.setTotal(total);

        BigDecimal paymentTotal = request.getPayments().stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (paymentTotal.compareTo(total) != 0) {
            throw new DuplicateResourceException(
                    "La suma de los pagos debe ser igual al total de la venta"
            );
        }

        for (var paymentRequest : request.getPayments()) {

            var paymentMethod = paymentMethodRepository
                    .findById(paymentRequest.getPaymentMethodId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "El método de pago no fue encontrado"
                            )
                    );

            if (!Boolean.TRUE.equals(paymentMethod.getActive())) {
                throw new DuplicateResourceException(
                        "El método de pago está inactivo: "
                                + paymentMethod.getName()
                );
            }
        }

        BigDecimal cashSales = BigDecimal.ZERO;
        BigDecimal transferSales = BigDecimal.ZERO;

        for (var paymentRequest : request.getPayments()) {

            var paymentMethod = paymentMethodRepository
                    .findById(paymentRequest.getPaymentMethodId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "El método de pago no fue encontrado"
                            )
                    );

            SalePayment payment = new SalePayment();

            payment.setSale(savedSale);
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(paymentRequest.getAmount());

            salePaymentRepository.save(payment);

            if ("EFECTIVO".equalsIgnoreCase(paymentMethod.getName())) {
                cashSales = cashSales.add(paymentRequest.getAmount());
            }

            if ("TRANSFERENCIA".equalsIgnoreCase(paymentMethod.getName())) {
                transferSales = transferSales.add(paymentRequest.getAmount());
            }
        }

        BigDecimal totalSales = cashSales.add(transferSales);

        BigDecimal expectedCash = cashRegister.getOpeningAmount()
                .add(cashRegister.getCashSales())
                .add(cashSales);

        cashRegister.setCashSales(
                cashRegister.getCashSales().add(cashSales)
        );

        cashRegister.setTransferSales(
                cashRegister.getTransferSales().add(transferSales)
        );

        cashRegister.setTotalSales(
                cashRegister.getTotalSales().add(totalSales)
        );

        cashRegister.setExpectedCash(expectedCash);

        cashRegisterRepository.save(cashRegister);

        savedSale.setStatus("COMPLETED");

        Sale finalSale = saleRepository.save(savedSale);

        return toResponse(finalSale);
    }

    private BigDecimal consumeFifo(
            SaleDetail detail,
            Product product,
            Integer quantityToConsume
    ) {

        int remainingQuantity = quantityToConsume;

        BigDecimal totalCost = BigDecimal.ZERO;

        var lots = inventoryLotRepository
                .findByProductIdAndAvailableQuantityGreaterThanAndActiveTrueOrderByEntryDateAscIdAsc(
                        product.getId(),
                        0
                );

        for (var lot : lots) {

            if (remainingQuantity == 0) {
                break;
            }

            int availableQuantity = lot.getAvailableQuantity();

            int quantityFromLot = Math.min(
                    remainingQuantity,
                    availableQuantity
            );

            BigDecimal lotCost = lot.getUnitCost()
                    .multiply(BigDecimal.valueOf(quantityFromLot));

            SaleDetailLot detailLot = new SaleDetailLot();

            detailLot.setSaleDetail(detail);
            detailLot.setInventoryLot(lot);
            detailLot.setQuantity(quantityFromLot);
            detailLot.setUnitCost(lot.getUnitCost());
            detailLot.setTotalCost(lotCost);

            saleDetailLotRepository.save(detailLot);

            lot.setAvailableQuantity(
                    availableQuantity - quantityFromLot
            );

            if (lot.getAvailableQuantity() == 0) {
                lot.setActive(false);
            }

            remainingQuantity -= quantityFromLot;
            totalCost = totalCost.add(lotCost);
        }

        if (remainingQuantity > 0) {
            throw new DuplicateResourceException(
                    "No hay suficiente inventario disponible para el producto: "
                            + product.getName()
            );
        }

        return totalCost;
    }

    public SaleResponse findById(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La venta no fue encontrada"
                        )
                );

        return toResponse(sale);
    }

    public List<SaleResponse> findAll() {
        return saleRepository.findAllByOrderBySaleDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SaleResponse cancelSale(Long id, SaleCancelRequest request) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La venta no fue encontrada"));

        if (!"COMPLETED".equals(sale.getStatus())) {
            throw new DuplicateResourceException(
                    "Solo se pueden cancelar ventas completadas"
            );
        }

        CashRegister cashRegister = cashRegisterRepository.findWithLockById(
                        sale.getCashRegister().getId())
                .orElseThrow(() -> new ResourceNotFoundException("La caja no fue encontrada"));

        if (!"OPEN".equals(cashRegister.getStatus())) {
            throw new DuplicateResourceException(
                    "No se puede cancelar la venta porque la caja está cerrada"
            );
        }

        List<SaleDetail> details = saleDetailRepository.findBySaleId(sale.getId());

        for (SaleDetail detail : details) {

            List<SaleDetailLot> lotConsumptions =
                    saleDetailLotRepository.findBySaleDetailId(detail.getId());

            for (SaleDetailLot lotConsumption : lotConsumptions) {

                InventoryLot lot = inventoryLotRepository
                        .findById(lotConsumption.getInventoryLot().getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("El lote de inventario no fue encontrado"));

                lot.setAvailableQuantity(
                        lot.getAvailableQuantity() + lotConsumption.getQuantity()
                );

                lot.setActive(true);

                inventoryLotRepository.save(lot);
            }

            Product product = productRepository.findWithLockById(
                            detail.getProduct().getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("El producto no fue encontrado"));

            product.setCurrentStock(
                    product.getCurrentStock() + detail.getQuantity()
            );

            productRepository.save(product);
        }

        List<SalePayment> payments = salePaymentRepository.findBySaleId(sale.getId());

        BigDecimal cashToReverse = BigDecimal.ZERO;
        BigDecimal transferToReverse = BigDecimal.ZERO;

        for (SalePayment payment : payments) {

            String paymentMethodName =
                    payment.getPaymentMethod().getName();

            if ("EFECTIVO".equalsIgnoreCase(paymentMethodName)) {
                cashToReverse = cashToReverse.add(payment.getAmount());
            }

            if ("TRANSFERENCIA".equalsIgnoreCase(paymentMethodName)) {
                transferToReverse = transferToReverse.add(payment.getAmount());
            }
        }

        cashRegister.setCashSales(
                cashRegister.getCashSales().subtract(cashToReverse)
        );

        cashRegister.setTransferSales(
                cashRegister.getTransferSales().subtract(transferToReverse)
        );

        cashRegister.setTotalSales(
                cashRegister.getTotalSales()
                        .subtract(cashToReverse)
                        .subtract(transferToReverse)
        );

        cashRegister.setExpectedCash(
                cashRegister.getOpeningAmount()
                        .add(cashRegister.getCashSales())
        );

        cashRegisterRepository.save(cashRegister);

        sale.setStatus("CANCELLED");

        if (request != null) {
            sale.setCancellationReason(request.getReason());
        }

        Sale cancelledSale = saleRepository.save(sale);

        return toResponse(cancelledSale);
    }

    private SaleResponse toResponse(Sale sale) {

        SaleResponse response = new SaleResponse();

        response.setId(sale.getId());
        response.setSaleNumber(sale.getSaleNumber());
        response.setCashRegisterId(sale.getCashRegister().getId());
        response.setUserId(sale.getUser().getId());
        response.setUsername(sale.getUser().getUsername());
        response.setSaleDate(sale.getSaleDate());
        response.setSubtotal(sale.getSubtotal());
        response.setDiscount(sale.getDiscount());
        response.setTotal(sale.getTotal());
        response.setStatus(sale.getStatus());
        response.setCancellationReason(sale.getCancellationReason());

        var details = saleDetailRepository.findBySaleId(sale.getId()).stream()
                .map(detail -> {
                    var detailResponse = new com.licorerajm.backend.dto.SaleDetailResponse();

                    detailResponse.setId(detail.getId());
                    detailResponse.setProductId(detail.getProduct().getId());
                    detailResponse.setProductName(detail.getProduct().getName());
                    detailResponse.setBarcode(detail.getProduct().getBarcode());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setUnitPrice(detail.getUnitPrice());
                    detailResponse.setDiscount(detail.getDiscount());
                    detailResponse.setSubtotal(detail.getSubtotal());
                    detailResponse.setTotal(detail.getTotal());
                    detailResponse.setUnitCost(detail.getUnitCost());
                    detailResponse.setTotalCost(detail.getTotalCost());
                    detailResponse.setProfit(detail.getProfit());

                    return detailResponse;
                })
                .toList();

        response.setItems(details);

        var payments = salePaymentRepository.findBySaleId(sale.getId())
                .stream()
                .map(payment -> {
                    var paymentResponse = new com.licorerajm.backend.dto.SalePaymentResponse();

                    paymentResponse.setId(payment.getId());
                    paymentResponse.setPaymentMethodId(
                            payment.getPaymentMethod().getId()
                    );
                    paymentResponse.setPaymentMethodName(
                            payment.getPaymentMethod().getName()
                    );
                    paymentResponse.setAmount(payment.getAmount());
                    paymentResponse.setPaymentDate(payment.getPaymentDate());

                    return paymentResponse;
                })
                .toList();

        response.setPayments(payments);

        return response;
    }

}