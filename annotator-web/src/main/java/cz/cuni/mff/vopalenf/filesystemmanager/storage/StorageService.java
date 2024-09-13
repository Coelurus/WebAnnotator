package cz.cuni.mff.vopalenf.filesystemmanager.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Service for storing, loading and deleting file system data.
 */
public interface StorageService {
    /**
     * Save file to a local disk.
     *
     * @param file File to be saved
     */
    void store(MultipartFile file);
    
}
