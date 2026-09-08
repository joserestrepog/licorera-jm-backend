package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CurrentUserResponse;
import com.licorerajm.backend.dto.InventoryEntryRequest;
import com.licorerajm.backend.dto.InventoryEntryResponse;
import com.licorerajm.backend.entity.InventoryEntry;
import com.licorerajm.backend.entity.InventoryLot;
import com.licorerajm.backend.entity.Product;
import com.licorerajm.backend.entity.User;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.InventoryEntryRepository;
import com.licorerajm.backend.repository.InventoryLotRepository;
import com.licorerajm.backend.repository.ProductRepository;
import com.licorerajm.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryEntryRepository inventoryEntryRepository;
    private final InventoryLotRepository inventoryLotRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public InventoryService(
            InventoryEntryRepository inventoryEntryRepository,
            InventoryLotRepository inventoryLotRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.inventoryEntryRepository = inventoryEntryRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public InventoryEntryResponse createEntry(InventoryEntryRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El producto no fue encontrado"));

        CurrentUserResponse currentUser = currentUserService.getCurrentUser();

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        InventoryEntry entry = new InventoryEntry();
        entry.setProduct(product);
        entry.setQuantity(request.getQuantity());
        entry.setPurchasePrice(request.getPurchasePrice());
        entry.setUser(user);
        entry.setNotes(request.getNotes());

        InventoryEntry savedEntry = inventoryEntryRepository.save(entry);

        InventoryLot lot = new InventoryLot();
        lot.setProduct(product);
        lot.setInventoryEntry(savedEntry);
        lot.setInitialQuantity(request.getQuantity());
        lot.setAvailableQuantity(request.getQuantity());
        lot.setUnitCost(request.getPurchasePrice());
        lot.setActive(true);

        inventoryLotRepository.save(lot);

        product.setCurrentStock(
                product.getCurrentStock() + request.getQuantity()
        );

        productRepository.save(product);

        return toResponse(savedEntry);
    }

    public List<InventoryEntryResponse> findAll() {
        return inventoryEntryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryEntryResponse findById(Long id) {
        InventoryEntry entry = inventoryEntryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La entrada de inventario no fue encontrada"));

        return toResponse(entry);
    }

    private InventoryEntryResponse toResponse(InventoryEntry entry) {

        Product product = entry.getProduct();
        User user = entry.getUser();

        return new InventoryEntryResponse(
                entry.getId(),
                product.getId(),
                product.getName(),
                product.getBarcode(),
                entry.getQuantity(),
                entry.getPurchasePrice(),
                entry.getEntryDate(),
                user.getId(),
                user.getUsername(),
                entry.getNotes()
        );
    }
}