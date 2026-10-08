package com.licorerajm.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class BackupService {

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    private static final String DATABASE_NAME = "licorera_jm";

    @Value("${app.backup.directory}")
    private String backupDirectory;

    @Value("${app.backup.retention-days}")
    private long backupRetentionDays;

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

        deleteExpiredBackups();

        return backupFile;
    }

    public List<Path> listBackups() throws IOException {

        Path backupDirectoryPath = Paths.get(backupDirectory);

        if (!Files.exists(backupDirectoryPath)) {
            return List.of();
        }

        try (var files = Files.list(backupDirectoryPath)) {

            return files
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .toLowerCase()
                                    .endsWith(".backup")
                    )
                    .sorted(
                            Comparator.comparing(
                                    path -> path.getFileName().toString(),
                                    Comparator.reverseOrder()
                            )
                    )
                    .toList();
        }
    }

    public void deleteExpiredBackups() throws IOException {

        Path backupDirectoryPath = Paths.get(backupDirectory);

        if (!Files.exists(backupDirectoryPath)) {
            return;
        }

        Instant expirationLimit = Instant.now()
                .minus(backupRetentionDays, ChronoUnit.DAYS);

        try (var files = Files.list(backupDirectoryPath)) {

            files
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .toLowerCase()
                                    .endsWith(".backup")
                    )
                    .forEach(path -> {

                        try {

                            Instant lastModified = Files.getLastModifiedTime(path)
                                    .toInstant();

                            if (lastModified.isBefore(expirationLimit)) {

                                Files.deleteIfExists(path);

                            }

                        } catch (IOException exception) {

                            throw new BackupCleanupException(
                                    "No fue posible eliminar el backup: "
                                            + path.getFileName(),
                                    exception
                            );
                        }
                    });
        }
    }

    private static class BackupCleanupException extends RuntimeException {

        public BackupCleanupException(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }
    }
}