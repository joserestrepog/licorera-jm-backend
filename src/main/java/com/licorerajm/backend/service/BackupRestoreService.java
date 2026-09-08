package com.licorerajm.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BackupRestoreService {

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${app.backup.directory}")
    private String backupDirectory;

    private static final String DATABASE_NAME = "licorera_jm";

    public void restoreBackup(String fileName)
            throws IOException, InterruptedException {

        Path backupDirectoryPath = Paths.get(backupDirectory);

        Path backupFile = backupDirectoryPath.resolve(fileName);

        if (!Files.exists(backupFile)) {
            throw new IllegalArgumentException(
                    "El archivo de backup no existe"
            );
        }

        if (!Files.isRegularFile(backupFile)) {
            throw new IllegalArgumentException(
                    "La ruta indicada no corresponde a un archivo"
            );
        }

        if (!fileName.toLowerCase().endsWith(".backup")) {
            throw new IllegalArgumentException(
                    "El archivo debe tener extensión .backup"
            );
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                "pg_restore",
                "-U", databaseUsername,
                "--clean",
                "--if-exists",
                "--no-owner",
                "-d", DATABASE_NAME,
                backupFile.toAbsolutePath().toString()
        );

        processBuilder.environment().put(
                "PGPASSWORD",
                databasePassword
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(
                    "No fue posible restaurar el backup de la base de datos"
            );
        }
    }
}