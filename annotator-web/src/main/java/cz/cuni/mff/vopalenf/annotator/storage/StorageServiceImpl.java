package cz.cuni.mff.vopalenf.annotator.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class StorageServiceImpl implements StorageService {

    /**
     * Root location of folder where all files are saved.
     */
    private final Path rootLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {

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

                //Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failure occurred during storing files...", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
