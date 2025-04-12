package ru.rsreu;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {
    public static void main(String[] args) {
        SymbolTable deserializedSymbolsTable;
        List<Instruction> deserializedInstructions;

        String combinedFilePath = "src/main/java/post_code.bin";
        try (FileInputStream fis = new FileInputStream(combinedFilePath);
             DataInputStream reader = new DataInputStream(fis)) {

            // Десериализация SymbolTable
            deserializedSymbolsTable = SymbolTable.deserialize(reader);
            System.out.println("Десериализованная таблица символов:");
            for (Map.Entry<Integer, Symbol> entry : deserializedSymbolsTable.getSymbols().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            // Десериализация списка инструкций
            System.out.println("Читаем количество инструкций:");
            int instructionCount = reader.readInt(); // Читаем количество инструкций
            System.out.println("Прочитали instructionCount= "+ instructionCount);
            deserializedInstructions = new ArrayList<>();
            for (int i = 0; i < instructionCount; i++) {
                Instruction inst = Instruction.deserialize(reader, deserializedSymbolsTable);
                //System.out.println("Добавляем i = " + i + " Инструкция:" + inst.toString());
                deserializedInstructions.add(inst); // Десериализуем каждую инструкцию
                System.out.println("Добавили");
            }

            System.out.println("Десериализация списка инструкций:");
            for (Instruction instruction : deserializedInstructions) {
                System.out.println(instruction.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Установка значений для переменных
        Scanner scanner = new Scanner(System.in);
        for (Map.Entry<Integer, Symbol> entry : deserializedSymbolsTable.getSymbols().entrySet()) {
            Symbol symbol = entry.getValue();

            // Пропускаем временные переменные (имя начинается с #T)
            if (!symbol.getName().startsWith("#T")) {
                boolean validInput = false;
                while (!validInput) {
                    System.out.println("Введите значение для переменной " + symbol.getName() + " (тип: " + symbol.getType() + "):");
                    String input = scanner.nextLine();

                    try {
                        // Преобразуем ввод в нужный тип
                        switch (symbol.getType()) {
                            case INT:
                                try {
                                    int intValue = Integer.parseInt(input);
                                    symbol.setValue(intValue);
                                    validInput = true; // Ввод корректен, выходим из цикла
                                } catch (NumberFormatException e) {
                                    System.out.println("Некорректный ввод! Пожалуйста, введите целое число.");
                                }
                                break;
                            //case FLOAT_TYPE:
                            case FLOAT:
                                try {
                                    float floatValue = Float.parseFloat(input);
                                    symbol.setValue(floatValue);
                                    validInput = true;
                                } catch (NumberFormatException e) {
                                    System.out.println("Некорректный ввод! Пожалуйста, введите число с плавающей точкой.");
                                }
                                break;
                            default:
                                symbol.setValue(input); // По умолчанию строка
                                validInput = true;
                                break;
                        }
                    } catch (Exception ex) {
                        System.out.println("Ошибка: " + ex.getMessage() + ". Попробуйте снова.");
                    }
                }

                System.out.println("Для " + symbol.getName() + " установлено значение: " + symbol.getValue() +
                        " (тип: " + symbol.getValue().getClass().getSimpleName() + ")");
            }
        }

        // Выполнение инструкций с помощью Calculator
        Calculator calculator = new Calculator(deserializedSymbolsTable, deserializedInstructions);
        calculator.executeInstructions();
    }
}