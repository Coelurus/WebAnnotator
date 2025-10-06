package cz.cuni.mff.vopalenf.annotator.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * Service for storing, loading and deleting file system data.
 */
public interface StorageManager {
    /**
     * Save file to a local disk.
     *
     * @param file
     *            File to be saved
     */
    void store(MultipartFile file);

    /**
     * Delete file from a local disk.
     *
     * @param filename
     *            Name of file to be deleted
     */
    void delete(String filename);

    /**
     * Load file from a local disk.
     *
     * @param filename
     *            Name of file to be loaded
     * @return File as byte array
     */
    byte[] load(String filename);

    /**
     * Load file from a local disk as a stream for memory-efficient reading.
     *
     * @param filename
     *            Name of file to be loaded
     * @return InputStream for the file
     */
    InputStream loadAsStream(String filename);
}
