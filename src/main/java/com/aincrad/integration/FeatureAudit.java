package com.aincrad.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Audits all source files for TODO, FIXME, HACK, and XXX comments.
 * <p>
 * Reports every occurrence with file path and line number so developers
 * can prioritize which stubs to implement next.
 * </p>
 */
public final class FeatureAudit {

    private static final Pattern TODO_PATTERN = Pattern.compile(
        "//\\s*(TODO|FIXME|HACK|XXX)\\s*:?\\s*(.*)"
    );

    private FeatureAudit() {}

    public static void main(String[] args) throws IOException {
        System.out.println("=== Aincrad Feature Audit ===\n");

        Path srcRoot = Paths.get("src/main/java");
        List<TodoEntry> todos = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(srcRoot)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                List<String> lines = Files.readAllLines(path);
                for (int i = 0; i < lines.size(); i++) {
                    Matcher m = TODO_PATTERN.matcher(lines.get(i));
                    if (m.find()) {
                        todos.add(new TodoEntry(
                            path.toString(),
                            i + 1,
                            m.group(1),
                            m.group(2).trim()
                        ));
                    }
                }
            }
        }

        // Report by category
        reportByCategory(todos, "TODO");
        reportByCategory(todos, "FIXME");
        reportByCategory(todos, "HACK");
        reportByCategory(todos, "XXX");

        System.out.println("\n=== Feature Audit Summary ===");
        System.out.println("Total TODO/FIXME/HACK/XXX comments: " + todos.size());

        if (todos.isEmpty()) {
            System.out.println("NO MISSING FEATURES DETECTED");
        } else {
            long critical = todos.stream()
                .filter(t -> t.tag.equals("FIXME") || t.tag.equals("HACK"))
                .count();
            System.out.println("Critical (FIXME/HACK): " + critical);
            System.out.println("Remaining: " + (todos.size() - critical));
            System.out.println("STUBS PRESENT — review before release");
        }
    }

    private static void reportByCategory(List<TodoEntry> todos, String tag) {
        List<TodoEntry> filtered = todos.stream()
            .filter(t -> t.tag.equals(tag))
            .toList();

        if (filtered.isEmpty()) return;

        System.out.println("--- " + tag + " (" + filtered.size() + ") ---");
        for (TodoEntry entry : filtered) {
            String relativePath = entry.file.replace("src/main/java/com/aincrad/", "");
            System.out.println("  " + relativePath + ":" + entry.lineNumber
                + " — " + entry.message);
        }
        System.out.println();
    }

    private record TodoEntry(String file, int lineNumber, String tag, String message) {}
}
