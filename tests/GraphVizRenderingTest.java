package veryFastPDF.pdfProcessors;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eas.miscellaneous.StaticMethods;

/** Renderer failure regressions using deterministic child processes, without requiring Graphviz. */
public final class GraphVizRenderingTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) {
            child(args[1], Path.of(args[2]));
            return;
        }
        Path directory = Files.createTempDirectory("graphviz test ");
        Path output = Files.createFile(directory.resolve("render output.svg"));
        try {
            check(StaticMethods.readTextFromFile(output.toFile(), null).isEmpty(), "empty text file reads as empty");
            Files.writeString(output, "first\nsecond\n", StandardCharsets.UTF_8);
            check(StaticMethods.readTextFromFile(output.toFile(), null).equals("first\nsecond"),
                    "nonempty text joining preserves previous behavior");
            Files.write(output, new byte[0]);
            byte[] svg = GraphViz.runRenderer(command("svg", output), output);
            check(new String(svg, StandardCharsets.UTF_8).contains("Gr\u00fc\u00dfe"), "UTF-8 SVG preserved");
            byte[] binary = GraphViz.runRenderer(command("binary", output), output);
            check(Arrays.equals(binary, new byte[] {0, 1, (byte) 255, 10}), "binary render bytes preserved");
            Files.write(output, new byte[0]);
            failure(command("failure", output), output, "code 7", "syntax error");
            // A stale partial file must not turn a failing renderer into apparent success.
            Files.writeString(output, "<svg/>");
            failure(command("failure", output), output, "code 7", "syntax error");
            Files.write(output, new byte[0]);
            failure(command("empty", output), output, "code 0", "produced no output", "no diagnostics");
            failure(command("empty-warning", output), output, "produced no output", "plugin not available");
            failure(command("verbose", output), output, "code 8", "diagnostics truncated");
            Files.delete(output);
            failure(command("empty", output), output, "produced no output");
            failure(List.of(directory.resolve("missing-dot-executable").toString()), output, "missing-dot-executable");
            Files.createFile(output);
            Thread.currentThread().interrupt();
            try {
                GraphViz.runRenderer(command("empty", output), output);
                throw new AssertionError("Expected interruption");
            } catch (IOException expected) {
                check(expected.getMessage().contains("interrupted"), "interruption reported");
                check(Thread.currentThread().isInterrupted(), "interrupt status restored");
            } finally {
                Thread.interrupted();
            }
            try (var files = Files.list(directory)) {
                check(files.count() == 1, "diagnostic temporary files cleaned on success and failure");
            }
            GraphViz renderer = new GraphViz(null, null);
            renderer.assignTempDir(directory.toString());
            Field dot = GraphViz.class.getDeclaredField("dotFile");
            dot.setAccessible(true);
            Object previous = dot.get(null);
            dot.set(null, directory.resolve("missing-dot-executable").toString());
            try {
                for (boolean text : new boolean[] {true, false}) {
                    try {
                        if (text) renderer.getPlainGraphSVG("digraph { a -> b }");
                        else renderer.getGraph("digraph { a -> b }", "pdf");
                        throw new AssertionError("Expected missing renderer error");
                    } catch (IllegalStateException expected) {
                        check(expected.getMessage().contains("rendering failed") && expected.getCause() instanceof IOException,
                                "public rendering API preserves actionable error and cause");
                    }
                }
            } finally {
                dot.set(null, previous);
            }
            try (var files = Files.list(directory)) {
                check(files.count() == 1, "public render APIs clean DOT, output and log files on failure");
            }
        } finally {
            Files.deleteIfExists(output);
            Files.delete(directory);
        }
        System.out.println("Passed " + checks + " Graphviz checks.");
    }

    private static List<String> command(String mode, Path output) {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> command = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin", executable)
                .toString(), "-cp", System.getProperty("java.class.path"), GraphVizRenderingTest.class.getName()));
        command.addAll(List.of("child", mode, output.toString()));
        return command;
    }

    private static void failure(List<String> command, Path output, String... messages) throws IOException {
        try {
            GraphViz.runRenderer(command, output);
            throw new AssertionError("Expected renderer failure");
        } catch (IOException expected) {
            for (String message : messages) {
                check(expected.getMessage().contains(message), "diagnostic includes " + message + ": " + expected);
            }
        }
    }

    private static void child(String mode, Path output) throws IOException {
        switch (mode) {
        case "svg" -> Files.writeString(output, "<svg><text>Gr\u00fc\u00dfe</text></svg>", StandardCharsets.UTF_8);
        case "binary" -> Files.write(output, new byte[] {0, 1, (byte) 255, 10});
        case "failure" -> {
            System.err.println("syntax error in DOT input");
            System.exit(7);
        }
        case "empty-warning" -> System.err.println("SVG plugin not available");
        case "verbose" -> {
            for (int i = 0; i < 20000; i++) System.err.println("renderer diagnostic " + i);
            System.exit(8);
        }
        case "empty" -> { }
        default -> throw new IllegalArgumentException(mode);
        }
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
