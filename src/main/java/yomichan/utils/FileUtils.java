package yomichan.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import yomichan.exception.YomichanException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

    public static File getFile(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null!");
        }
        final File file = new File(path);
        if (!file.exists()) {
            throw new YomichanException("File does not exist at path " + path);
        }
        return file;
    }

    public static boolean delete(String path) {
        return delete(new File(path));
    }

    public static List<File> getFiles(final String path, final FilenameFilter filter) {
        File file = new File(path);
        final File[] files = file.listFiles(filter);
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toList();
    }
}
