package com.licorerajm.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    private static final String DATABASE_NAME = "licorera_jm";
    @Value("${app.backup.directory}")
    private String backupDirectory;

    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public Path createBackup() throws IOException, InterruptedException {

        Path backupDirectoryPath = Paths.get(backupDirectory);

        Files.createDirectories(backupDirectoryPath);

        String fileName = "licorera_jm_"
                + LocalDateTime.now().format(FILE_DATE_FORMAT)
                + ".backup";

        Path backupFile = backupDirectoryPath.resolve(fileName);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "pg_dump",
                "-U", databaseUsername,
                "-F", "c",
                "-f", backupFile.toAbsolutePath().toString(),
                DATABASE_NAME
        );

        processBuilder.environment().put(
                "PGPASSWORD",
                databasePassword
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            Files.deleteIfExists(backupFile);

            throw new IllegalStateException(
                    "No fue posible crear el backup de la base de datos"
            );
        }

        return backupFile;
    }
}