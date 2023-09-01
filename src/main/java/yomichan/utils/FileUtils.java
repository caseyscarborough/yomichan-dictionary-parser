package yomichan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static String getTempFolder() {
        String tmp = System.getProperty("java.io.tmpdir");
        if (tmp.endsWith("/")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        return String.format("%s/%s", tmp, UUID.randomUUID());
    }

    public static boolean delete(File file) {
        try {
            final boolean result = Files.deleteIfExists(file.toPath());
            log.debug("Successfully deleted {}", file.getAbsolutePath());
            return result;
        } catch (IOException e) {
            log.warn("Could not delete file {}", file.getAbsolutePath(), e);
            return false;
        }
    }

    public static boolean delete(String path) {
        return delete(new File(path));
    }
}
