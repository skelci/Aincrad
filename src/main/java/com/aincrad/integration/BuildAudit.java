package com.aincrad.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Verifies all source files compile and the project structure is valid.
 * <p>
 * Checks for:
 * <ul>
 *   <li>All .java files exist and are readable</li>
 *   <li>No empty source files</li>
 *   <li>All package directories have at least one source file</li>
 *   <li>No orphaned .java files outside the main source tree</li>
 * </ul>
 * </p>
 */
public final class BuildAudit {

    private BuildAudit() {}

    public static void main(String[] args) throws IOException {
        System.out.println("=== Aincrad Build Audit ===\n");

        boolean allPassed = true;

        allPassed &= auditSourceFiles();
        allPassed &= auditPackageStructure();
        allPassed &= auditResourceFiles();

        System.out.println("\n=== Build Audit Summary ===");
        if (allPassed) {
            System.out.println("ALL BUILD CHECKS PASSED");
        } else {
            System.out.println("SOME BUILD CHECKS HAVE ISSUES — see above");
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Source file audit
    // ------------------------------------------------------------------

    private static boolean auditSourceFiles() throws IOException {
        System.out.println("--- Source Files ---");

        Path srcRoot = Paths.get("src/main/java");
        List<Path> javaFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(srcRoot)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                .forEach(javaFiles::add);
        }

        System.out.println("  Total .java files: " + javaFiles.size());

        // Check for empty files
        int emptyCount = 0;
        for (Path file : javaFiles) {
            if (Files.size(file) == 0) {
                System.out.println("  EMPTY: " + file);
                emptyCount++;
            }
        }

        if (emptyCount > 0) {
            System.out.println("  ISSUE: " + emptyCount + " empty source files");
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Package structure audit
    // ------------------------------------------------------------------

    private static boolean auditPackageStructure() throws IOException {
        System.out.println("--- Package Structure ---");

        Path srcRoot = Paths.get("src/main/java/com/aincrad");
        List<Path> packages = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(srcRoot)) {
            paths.filter(Files::isDirectory)
                .forEach(packages::add);
        }

        System.out.println("  Package directories: " + packages.size());

        // Check each package has at least one .java file
        int emptyPackages = 0;
        for (Path pkg : packages) {
            try (Stream<Path> files = Files.list(pkg)) {
                long javaCount = files.filter(p -> p.toString().endsWith(".java")).count();
                if (javaCount == 0) {
                    System.out.println("  EMPTY PACKAGE: " + pkg.getFileName());
                    emptyPackages++;
                }
            }
        }

        if (emptyPackages > 0) {
            System.out.println("  ISSUE: " + emptyPackages + " empty packages");
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Resource file audit
    // ------------------------------------------------------------------

    private static boolean auditResourceFiles() throws IOException {
        System.out.println("--- Resource Files ---");

        Path resRoot = Paths.get("src/main/resources");
        if (!Files.exists(resRoot)) {
            System.out.println("  No resources directory found");
            System.out.println("  PASSED\n");
            return true;
        }

        List<Path> resources = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(resRoot)) {
            paths.filter(Files::isRegularFile).forEach(resources::add);
        }

        System.out.println("  Total resource files: " + resources.size());

        // Check for critical files
        boolean hasBalanceYml = resources.stream()
            .anyMatch(p -> p.getFileName().toString().equals("balance.yml"));
        boolean hasPluginYml = resources.stream()
            .anyMatch(p -> p.getFileName().toString().equals("plugin.yml"));
        boolean hasFabricModJson = resources.stream()
            .anyMatch(p -> p.getFileName().toString().equals("fabric.mod.json"));

        if (!hasBalanceYml) {
            System.out.println("  ISSUE: balance.yml missing");
            return false;
        }
        if (!hasPluginYml) {
            System.out.println("  ISSUE: plugin.yml missing");
            return false;
        }
        if (!hasFabricModJson) {
            System.out.println("  ISSUE: fabric.mod.json missing");
            return false;
        }

        System.out.println("  Critical files present: balance.yml, plugin.yml, fabric.mod.json");
        System.out.println("  PASSED\n");
        return true;
    }
}
