package ru.rsreu;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileWriter {

    public void writeFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            writer.write(content);
            writer.newLine();
        }
    }

    public static void writeStringToFile(String fileName, String outputString) throws IOException {
        try {
            Path path = new File(fileName).toPath();
            // Запись строк в файл с новой строки для каждой строки массива
            Files.write(path, outputString.getBytes());
        } catch (IOException ex) {
            throw new IOException("Файл " + fileName + " не может быть открыт или записан.");
        }
    }

    public static void writeThreeAddressCodeToFile(List<Instruction> threeAddressCode, String fileName) throws IOException {
        List<String> lines = threeAddressCode.stream()
                .map(Instruction::toString)
                .collect(Collectors.toList());
        Files.write(Paths.get(fileName), lines);
    }

    public static void writePostfixFormToFile(List<Token> postfixForm, String fileName) throws IOException {
        String line = postfixForm.stream()
                .map(token -> token.toString().split(" - ")[0]) // Берем часть до " - "
                .collect(Collectors.joining("")); // Соединяем все токены без пробелов

        // Записываем строку в файл
        Files.write(Paths.get(fileName), line.getBytes());
    }

    public static void createBinFile(SymbolTable symbolTable, List<Instruction> instructions, String fileName) {
        System.out.println("createBinFile");
        try (FileOutputStream fos = new FileOutputStream(fileName);
             DataOutputStream writer = new DataOutputStream(fos)) {

            // Сериализация таблицы символов (symbolTable)
            symbolTable.serialize(writer);
            System.out.println("SymbolTable сериализована в " + fileName);

            // Сериализация списка инструкций (List<Instruction>)
            writer.writeInt(instructions.size());
            for (Instruction instruction : instructions) {
                System.out.println("instr= " + instruction.toString());
                instruction.serialize(writer);
                System.out.println("instr= " + instruction.toString() + " Сериализовалась");
            }
            System.out.println("Список инструкций сериализован в " + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка во время генерации bin-файла: " + e.getMessage());
        }
    }
}
