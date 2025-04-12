package ru.rsreu;

import java.io.IOException;
import java.util.List;

public class ExpressionTranslator {

    private final FileReader fileReader = new FileReader();
    private final FileWriter fileWriter = new FileWriter();

    public void translate(String inputFileName, String outputFileName) throws IOException {
        List<String> lines = fileReader.readFile(inputFileName);
        StringBuilder translatedContent = new StringBuilder();

        for (String line : lines) {
            StringBuilder translatedExpression = new StringBuilder();

            for (char ch : line.toCharArray()) {
                if (Character.isDigit(ch)) {
                    int number = Character.getNumericValue(ch);
                    String numberWord = NumberWord.fromNumber(number);
                    translatedExpression.append(numberWord).append(" ");
                } else if (Operator.fromSymbol(ch) != null) {
                    Operator operator = Operator.fromSymbol(ch);
                    translatedExpression.append(operator.getWord()).append(" ");
                } else if (Character.isWhitespace(ch)) {
                    translatedExpression.append(ch);
                } else {
                    System.out.println("Ошибка: недопустимый символ '" + ch + "' в строке: " + line);
                    return;
                }
            }
            translatedContent.append(translatedExpression.toString().trim()).append("\n");
        }

        fileWriter.writeFile(outputFileName, translatedContent.toString());
        System.out.println("Трансляция завершена. Результат записан в " + outputFileName);
    }
}
