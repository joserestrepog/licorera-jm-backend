package com.licorerajm.backend;

import com.licorerajm.backend.service.BackupService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BackupServiceTest {

    @Test
    void shouldCreateBackup() throws Exception {

        BackupService backupService = new BackupService();

        ReflectionTestUtils.setField(
                backupService,
                "databaseUsername",
                "postgres"
        );

        String databasePassword =
                System.getenv("LICORERA_JM_DB_PASSWORD");

        if (databasePassword == null || databasePassword.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno LICORERA_JM_DB_PASSWORD no está configurada."
            );
        }

        ReflectionTestUtils.setField(
                backupService,
                "databasePassword",
                databasePassword
        );

        ReflectionTestUtils.setField(
                backupService,
                "backupDirectory",
                "backups"
        );

        Path backupFile = backupService.createBackup();

        assertTrue(
                backupFile.toFile().exists(),
                "El archivo de backup debe existir"
        );
    }
}