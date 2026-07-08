package com.thejaswini.flightbooking.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Small standalone utility that zips the project's sources, build scripts, docs, and README into
 * {@code dist/flight-booking-<timestamp>.zip}, skipping generated/heavy directories (build output,
 * VCS, logs, previous archives, IDE metadata).
 *
 * <p>Run via the Gradle task {@code ./gradlew runArchiver} or directly with a Java launcher.
 */
public final class ProjectArchiver {

    /** Directory names that are never archived. */
    private static final List<String> EXCLUDED_DIRS =
            List.of("build", ".gradle", ".git", "logs", "dist", ".idea", "bin", "out");

    /**
     * Prevents instantiation of this utility class.
     */
    private ProjectArchiver() {
    }

    /**
     * Archives the project rooted at the current working directory.
     *
     * @param args optional single argument = output zip path; defaults to a timestamped file under {@code dist/}
     * @throws IOException if reading the tree or writing the archive fails
     */
    public static void main(String[] args) throws IOException {
        Path root = Paths.get("").toAbsolutePath();
        Path output = (args.length > 0)
                ? Paths.get(args[0])
                : root.resolve("dist").resolve("flight-booking-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".zip");
        Files.createDirectories(output.getParent());

        int count = archive(root, output);
        System.out.printf("Archived %d files into %s%n", count, output);
    }

    /**
     * Walks the tree under {@code root} and writes every non-excluded file into a zip at {@code output}.
     *
     * @param root   project root to archive
     * @param output destination zip path
     * @return the number of files written
     * @throws IOException on I/O failure
     */
    static int archive(Path root, Path output) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(output))) {
            ArchivingVisitor visitor = new ArchivingVisitor(root, output, zip);
            Files.walkFileTree(root, visitor);
            return visitor.fileCount();
        }
    }

    /**
     * File visitor that skips excluded directories and adds every other file as a zip entry.
     */
    private static final class ArchivingVisitor extends SimpleFileVisitor<Path> {

        private final Path root;
        private final Path output;
        private final ZipOutputStream zip;
        private int fileCount;

        /**
         * @param root   project root being archived
         * @param output the archive being written (skipped if encountered during the walk)
         * @param zip    the open zip stream to write entries into
         */
        ArchivingVisitor(Path root, Path output, ZipOutputStream zip) {
            this.root = root;
            this.output = output;
            this.zip = zip;
        }

        /** @return how many files were added to the archive */
        int fileCount() {
            return fileCount;
        }

        /**
         * Skips excluded directories (build output, VCS, logs, previous archives, IDE metadata).
         *
         * @param dir   the directory about to be visited
         * @param attrs its attributes
         * @return {@link FileVisitResult#SKIP_SUBTREE} for excluded dirs, otherwise continue
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            if (!dir.equals(root) && EXCLUDED_DIRS.contains(dir.getFileName().toString())) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * Adds each visited file (except the archive being written) as a zip entry.
         *
         * @param file  the file being visited
         * @param attrs its attributes
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException if the file cannot be read or written into the archive
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.equals(output)) {
                return FileVisitResult.CONTINUE;
            }
            String entryName = root.relativize(file).toString().replace('\\', '/');
            zip.putNextEntry(new ZipEntry(entryName));
            Files.copy(file, zip);
            zip.closeEntry();
            fileCount++;
            return FileVisitResult.CONTINUE;
        }
    }
}
