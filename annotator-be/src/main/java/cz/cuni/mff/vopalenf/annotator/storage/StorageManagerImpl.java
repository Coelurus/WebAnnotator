package cz.cuni.mff.vopalenf.annotator.storage;

import cz.cuni.mff.vopalenf.annotator.config.StorageConfig;
import cz.cuni.mff.vopalenf.annotator.exception.StorageException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
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
     * Path to the file system where files are stored. This value is read from
     * application properties.
     */
    @Value("${app.file-system.path:file_system}")
    String fileSystemPath;

    /**
     * Root location of folder where all files are saved.
     */
    private Path rootLocation;

    /**
     * Constructs a new StorageManagerImpl with the specified properties.
     *
     * @param properties
     *            the storage configuration properties
     */
    @Autowired
    public StorageManagerImpl(StorageConfig properties) {
        // Constructor does not initialize rootLocation here anymore
        // It will be initialized in the @PostConstruct method after @Value injection
    }

    /**
     * Initialize the root location after Spring has injected the fileSystemPath value.
     */
    @PostConstruct
    private void init() {
        if (fileSystemPath == null || fileSystemPath.trim().isEmpty()) {
            throw new StorageException("File upload location must not be empty!");
        }
        rootLocation = Paths.get(fileSystemPath);
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
                    Path entryDestination = zipFileDirectory.resolve(Paths.get(zipEntry.getName())).normalize()
                            .toAbsolutePath();

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
     * @param dirToDelete
     *            Name of the dir in root of filesystem
     * @throws StorageException
     *             When failure occurred during deleting files
     */
    private void deleteDirIfExists(String dirToDelete) {
        Path targetDir = this.rootLocation.resolve(dirToDelete);

        if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
            deleteDirRecursively(targetDir);
        } else {
            // Do nothing as the directory is already 'deleted'
        }
    }

    @Override
    public byte[] load(String filename) {
        if (filename.isEmpty()) {
            throw new StorageException("Filename must not be empty!");
        }
        Path filePath = this.rootLocation.resolve(filename);
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new StorageException("Failure occurred during loading files...", e);
        }
    }

    @Override
    public InputStream loadAsStream(String filename) {
        if (filename.isEmpty()) {
            throw new StorageException("Filename must not be empty!");
        }
        Path filePath = this.rootLocation.resolve(filename);
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new StorageException("Failure occurred during loading files...", e);
        }
    }

    /**
     * Delete directory and everything inside it
     *
     * @param targetDir
     *            Path to a directory to delete
     * @throws StorageException
     *             When exception occurs during file deleting
     */
    private void deleteDirRecursively(Path targetDir) {
        try {
            FileUtils.deleteDirectory(targetDir.toFile());
        } catch (IOException e) {
            throw new StorageException("Failure occurred during deleting files...", e);
        }
    }
}
