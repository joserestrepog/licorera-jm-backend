package com.licorerajm.backend.controller;

import com.licorerajm.backend.service.BackupRestoreService;
import com.licorerajm.backend.service.BackupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backups")
public class BackupController {

    private final BackupService backupService;
    private final BackupRestoreService backupRestoreService;

    public BackupController(
            BackupService backupService,
            BackupRestoreService backupRestoreService
    ) {
        this.backupService = backupService;
        this.backupRestoreService = backupRestoreService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createBackup()
            throws IOException, InterruptedException {

        Path backupFile = backupService.createBackup();

        return Map.of(
                "message", "Backup creado correctamente",
                "fileName", backupFile.getFileName().toString()
        );
    }

    @GetMapping
    public List<String> listBackups() throws IOException {

        return backupService.listBackups()
                .stream()
                .map(path -> path.getFileName().toString())
                .toList();
    }

    @PostMapping("/restore")
    public Map<String, String> restoreBackup(
            @RequestParam String fileName
    ) throws IOException, InterruptedException {

        backupRestoreService.restoreBackup(fileName);

        return Map.of(
                "message", "Backup restaurado correctamente",
                "fileName", fileName
        );
    }
}