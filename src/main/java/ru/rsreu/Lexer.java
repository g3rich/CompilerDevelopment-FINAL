package ru.rsreu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final SymbolTable symbolTable;
    private final List<Token> tokens;
    private final FileReader fileReader;
    private final FileWriter fileWriter;

    public Lexer() {
        this.symbolTable = new SymbolTable();
        this.tokens = new ArrayList<>();
        this.fileReader = new FileReader();
        this.fileWriter = new FileWriter();
    }

    public List<Token> analyze(String inputFile, String tokensFile, String symbolsFile) throws IOException {
        List<String> lines = fileReader.readFile(inputFile);
        StringBuilder fullInput = new StringBuilder();

        for (String line : lines) {
            fullInput.append(line.replaceAll("\\s+", ""));
        }

        int position = 0;
        while (position < fullInput.length()) {
            char currentChar = fullInput.charAt(position);

            // Обработка идентификаторов и ключевых слов
            if (Character.isLetter(currentChar) || currentChar == '_') {
                StringBuilder identifier = new StringBuilder();
                while (position < fullInput.length() &&
                        (Character.isLetterOrDigit(fullInput.charAt(position)) || fullInput.charAt(position) == '_')) {
                    identifier.append(fullInput.charAt(position++));
                }
                String id = identifier.toString();

                // Проверяем, есть ли после идентификатора квадратные скобки с типом
                String type = "целый"; // Тип по умолчанию
                if (position < fullInput.length() && fullInput.charAt(position) == '[') {
                    position++; // Пропускаем '['
                    if (position < fullInput.length()) {
                        //System.out.println("ya zdes" + fullInput.charAt(position) + "-" + position);
                        char typeChar = fullInput.charAt(position);
                        if (typeChar == 'f' || typeChar == 'F') {
                            //System.out.println("ya zdes2" + fullInput.charAt(position) + "-" + position);
                            type = "вещественный";
                        } else if (typeChar != 'i' && typeChar != 'I') {
                            throw new IllegalArgumentException("Лексическая ошибка! Неправильный тип переменной после " + id + " на позиции " + (position + 1));
                        }
                        position++; // Пропускаем символ типа
                    }
                    if (position < fullInput.length() && fullInput.charAt(position) == ']') {
                        position++; // Пропускаем ']'
                    } else {
                        throw new IllegalArgumentException("Лексическая ошибка! Ожидалась закрывающая скобка после типа переменной " + id + " на позиции " + position);
                    }
                }
                //System.out.println("ya zdes3" + type);

                // Добавляем переменную в таблицу символов с её типом
                int symbolId = symbolTable.addSymbol(id, type);
                tokens.add(new Token(TokenType.ID, String.valueOf(symbolId), symbolId)); // Добавляем токен
            }
            // Обработка чисел
            else if (Character.isDigit(currentChar)) {
                StringBuilder number = new StringBuilder();
                boolean isFloat = false;
                if ((position + 1) < fullInput.length()) {
                    if (Character.isLetter(fullInput.charAt(position + 1))) {
                        int outputPosition = position + 1;
                        throw new IllegalArgumentException("Лексическая ошибка! Идентификатор не может начинаться с цифры на позиции " + outputPosition);
                    }
                }
                while (position < fullInput.length() &&
                        (Character.isDigit(fullInput.charAt(position)) || fullInput.charAt(position) == '.')) {
                    /*if (Character.isLetter(fullInput.charAt(position + 1))) {
                        int outputPosition = position + 1;
                        throw new IllegalArgumentException("Лексическая ошибка! Идентификатор не может начинаться с цифры на позиции " + outputPosition);
                    }*/
                    if (fullInput.charAt(position) == '.') {
                        if (isFloat) {
                            int outputPosition = position + 1;
                            throw new IllegalArgumentException("Лексическая ошибка! Неправильно задана константа на позиции " + outputPosition);
                        }
                        isFloat = true;
                    }
                    number.append(fullInput.charAt(position++));
                }
                tokens.add(new Token(isFloat ? TokenType.FLOAT : TokenType.INT, number.toString(), 0));
            }
            else if ("+-*/".indexOf(currentChar) != -1) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(currentChar), 0));
                position++;
            }
            else if ("()".indexOf(currentChar) != -1) {
                tokens.add(new Token(TokenType.PARENTHESIS, String.valueOf(currentChar), 0));
                position++;
            } else {
                int outputPosition = position + 1;
                throw new IllegalArgumentException("Лексическая ошибка! Недопустимый символ '" + currentChar + "' на позиции " + outputPosition++);
            }
        }

        if ((!tokensFile.isEmpty()) || (!symbolsFile.isEmpty())) {
            if (!symbolsFile.isEmpty()) {
                writeSymbols(symbolsFile);
            }
            if (!tokensFile.isEmpty()) {
                writeTokens(tokensFile);
            }
            //writeTokens(tokensFile);
            //writeSymbols(symbolsFile);
        }
        return tokens;
    }

    private void writeTokens(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        for (Token token : tokens) {
            content.append(token).append("\n");
        }
        fileWriter.writeFile(fileName, content.toString());
    }

    private void writeSymbols(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        for (var entry : symbolTable.getSymbols().entrySet()) {
            int id = entry.getKey();
            Symbol symbol = entry.getValue();
            //String type = symbolTable.getTypes().get(id);
            content.append(id).append(" – ").append(symbol.getName()).append(" [").append(symbol.getType()).append("]").append("\n");
        }
        fileWriter.writeFile(fileName, content.toString());
    }

    public SymbolTable getSymbolTable(){
        return this.symbolTable;
    }


    public List<Token> getTokens() {
        return this.tokens;
    }
}
