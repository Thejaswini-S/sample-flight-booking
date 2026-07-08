package com.thejaswini.flightbooking.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProjectArchiver}: it archives regular files and skips excluded directories,
 * exercised against a temporary directory tree.
 */
@DisplayName("ProjectArchiver — source/doc zipping")
class ProjectArchiverTest {

    /** Archives a small tree and confirms included files are present and excluded dirs are skipped. */
    @Test
    @DisplayName("archives files and skips excluded directories")
    void archivesFilesSkippingExcludedDirs(@TempDir Path root) throws IOException {
        Files.writeString(root.resolve("README.md"), "readme");
        Files.createDirectories(root.resolve("src"));
        Files.writeString(root.resolve("src").resolve("App.java"), "class App {}");
        Files.createDirectories(root.resolve("build"));
        Files.writeString(root.resolve("build").resolve("ignored.class"), "nope");

        Path out = root.resolve("out").resolve("archive.zip");
        Files.createDirectories(out.getParent());

        int count = ProjectArchiver.archive(root, out);

        Set<String> entries = entryNames(out);
        assertThat(entries).contains("README.md", "src/App.java");
        assertThat(entries).noneMatch(name -> name.startsWith("build/"));
        assertThat(count).isEqualTo(entries.size());
    }

    /**
     * Collects the entry names contained in the given zip archive.
     *
     * @param zip the archive to read
     * @return the set of entry names
     * @throws IOException if the archive cannot be read
     */
    private static Set<String> entryNames(Path zip) throws IOException {
        Set<String> names = new HashSet<>();
        try (ZipFile zipFile = new ZipFile(zip.toFile())) {
            var entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                names.add(entry.getName());
            }
        }
        return names;
    }
}
