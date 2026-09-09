package com.licorerajm.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class BackupScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(BackupScheduler.class);

    private final BackupService backupService;

    public BackupScheduler(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(
            cron = "${app.backup.cron}",
            zone = "America/Bogota"
    )
    public void executeAutomaticBackup() {

        try {
            Path backupFile = backupService.createBackup();

            logger.info(
                    "Backup automático creado correctamente: {}",
                    backupFile.getFileName()
            );

        } catch (Exception exception) {

            logger.error(
                    "Error al crear el backup automático",
                    exception
            );
        }
    }
}