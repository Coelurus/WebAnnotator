package cz.cuni.mff.vopalenf.annotator.manager.storage;

import cz.cuni.mff.vopalenf.annotator.config.StorageConfig;
import cz.cuni.mff.vopalenf.annotator.exception.StorageException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implementation of service to store data from sensor and camera.
 */
@Service
public class StorageManagerImpl implements StorageManager {

    /**
     * Root location of folder where all files are saved.
     */
    private final Path rootLocation;

    @Autowired
    public StorageManagerImpl(StorageConfig properties) {

        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location must not be empty!");
        }
        rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("File is empty!");
            }

            // Get the original filename
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
            String baseDirectoryName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

            // Create a dir with the name of the zip file
            Path zipFileDirectory = rootLocation.resolve(baseDirectoryName).normalize().toAbsolutePath();
            Files.createDirectories(zipFileDirectory);

            // Unzipping files
            try (InputStream inputStream = file.getInputStream();
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    // Resolve path for each entry
                    Path entryDestination = zipFileDirectory.resolve(Paths.get(zipEntry.getName()))
                            .normalize().toAbsolutePath();

                    if (zipEntry.isDirectory()) {
                        Files.createDirectories(entryDestination);
                    } else {
                        // Creating parent directories if they do not exist
                        if (entryDestination.getParent() != null) {
                            Files.createDirectories(entryDestination.getParent());
                        }

                        // Extract the file
                        try (OutputStream outputStream = Files.newOutputStream(entryDestination)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zipInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                        }
                    }
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failure occurred during storing files...", e);
        }
    }

    @Override
    public void delete(String filename) {
        if (filename.isEmpty()) {
            throw new StorageException("Filename must not be empty!");
        }
        deleteDirIfExists(filename);
    }

    /**
     * Delete directory based on name in file system root
     *
     * @param dirToDelete Name of the dir in root of filesystem
     * @throws StorageException When failure occurred during deleting files
     */
    private void deleteDirIfExists(String dirToDelete) {
        Path targetDir = this.rootLocation.resolve(dirToDelete);

        if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
            deleteDirRecursively(targetDir);
        } else {
            // Do nothing as the directory is already 'deleted'
        }
    }

    /**
     * Delete directory and everything inside it
     *
     * @param targetDir Path to a directory to delete
     * @throws StorageException When exception occurs during file deleting
     */
    private void deleteDirRecursively(Path targetDir) {
        try {
            FileUtils.deleteDirectory(targetDir.toFile());
        } catch (IOException e) {
            throw new StorageException("Failure occurred during deleting files...", e);
        }
    }
}
