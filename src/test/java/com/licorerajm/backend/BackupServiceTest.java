package com.licorerajm.backend;

import com.licorerajm.backend.service.BackupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

        ReflectionTestUtils.setField(
                backupService,
                "backupRetentionDays",
                30L
        );

        Path backupFile = backupService.createBackup();

        assertTrue(
                backupFile.toFile().exists(),
                "El archivo de backup debe existir"
        );
    }

    @Test
    void shouldDeleteExpiredBackups(@TempDir Path temporaryDirectory)
            throws Exception {

        BackupService backupService = new BackupService();

        ReflectionTestUtils.setField(
                backupService,
                "backupDirectory",
                temporaryDirectory.toString()
        );

        ReflectionTestUtils.setField(
                backupService,
                "backupRetentionDays",
                30L
        );

        Path recentBackup = Files.createFile(
                temporaryDirectory.resolve(
                        "licorera_jm_recent.backup"
                )
        );

        Path expiredBackup = Files.createFile(
                temporaryDirectory.resolve(
                        "licorera_jm_expired.backup"
                )
        );

        Path otherFile = Files.createFile(
                temporaryDirectory.resolve(
                        "important-file.txt"
                )
        );

        Files.setLastModifiedTime(
                recentBackup,
                FileTime.from(
                        Instant.now().minus(29, ChronoUnit.DAYS)
                )
        );

        Files.setLastModifiedTime(
                expiredBackup,
                FileTime.from(
                        Instant.now().minus(31, ChronoUnit.DAYS)
                )
        );

        Files.setLastModifiedTime(
                otherFile,
                FileTime.from(
                        Instant.now().minus(60, ChronoUnit.DAYS)
                )
        );

        backupService.deleteExpiredBackups();

        assertTrue(
                Files.exists(recentBackup),
                "El backup de menos de 30 días debe conservarse"
        );

        assertFalse(
                Files.exists(expiredBackup),
                "El backup de más de 30 días debe eliminarse"
        );

        assertTrue(
                Files.exists(otherFile),
                "Los archivos que no son .backup no deben eliminarse"
        );
    }
}