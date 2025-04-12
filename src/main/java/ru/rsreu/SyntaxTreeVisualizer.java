package ru.rsreu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SyntaxTreeVisualizer {
    public static void visualize(SyntaxNode root, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            visualizeNode(writer, root, "", true);
        }
    }

    private static void visualizeNode(BufferedWriter writer, SyntaxNode node, String prefix, boolean isLast) throws IOException {
        if (node != null) {
            writer.write(prefix + (isLast ? "|---" : "|---") + node.toString() + "\n");
            visualizeNode(writer, node.getLeft(), prefix + (isLast ? "    " : "|---"), false);
            visualizeNode(writer, node.getRight(), prefix + (isLast ? "    " : "|---"), true);
        }
    }
}

