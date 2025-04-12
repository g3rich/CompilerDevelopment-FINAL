package ru.rsreu;

import java.io.IOException;
import java.util.Random;

public class ExpressionGenerator {

    private final Random random = new Random();
    private final FileWriter fileWriter = new FileWriter();

    public void generate(String fileName, int numLines, int minOperands, int maxOperands) {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < numLines; i++) {
            int numOperands = random.nextInt(maxOperands - minOperands + 1) + minOperands;
            StringBuilder expression = new StringBuilder();

            for (int j = 0; j < numOperands; j++) {
                expression.append(random.nextInt(9) + 1); // добавляем случайное число

                if (j < numOperands - 1) {
                    Operator operator = getRandomOperator();
                    expression.append(" ").append(operator.getSymbol()).append(" ");
                }
            }
            content.append(expression.toString()).append("\n");
        }

        try {
            fileWriter.writeFile(fileName, content.toString());
            System.out.println("Арифметические выражения сгенерированы и записаны в " + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка: не удалось записать в файл.");
        }
    }

    private Operator getRandomOperator() {
        Operator[] operators = Operator.values();
        return operators[random.nextInt(operators.length)];
    }
}
