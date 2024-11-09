package cz.cuni.mff.vopalenf.annotator.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for storing, loading and deleting file system data.
 */
public interface StorageManager {
    /**
     * Save file to a local disk.
     *
     * @param file File to be saved
     */
    void store(MultipartFile file);
    
}
