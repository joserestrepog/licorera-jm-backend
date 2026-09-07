package com.licorerajm.backend.controller;

import com.licorerajm.backend.dto.InventoryEntryRequest;
import com.licorerajm.backend.dto.InventoryEntryResponse;
import com.licorerajm.backend.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/entries")
    public List<InventoryEntryResponse> findAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/entries/{id}")
    public InventoryEntryResponse findById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryEntryResponse createEntry(
            @Valid @RequestBody InventoryEntryRequest request
    ) {
        return inventoryService.createEntry(request);
    }
}