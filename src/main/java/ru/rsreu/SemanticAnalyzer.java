package ru.rsreu;

public class SemanticAnalyzer {

    /*public void analyze(SyntaxNode root) {
        if (root == null) return;

        // Рекурсивный обход синтаксического дерева
        analyzeNode(root);

        // Повторный обход для анализа
        checkForTypeMismatches(root);
    }

    private void analyzeNode(SyntaxNode node) {
        if (node == null) return;

        Token token = node.getToken();
        TokenType type = token.getType();

        // Здесь можно выполнить дополнительные проверки типов переменных или значений

        // Рекурсивный обход для левого и правого поддерева
        analyzeNode(node.getLeft());
        analyzeNode(node.getRight());
    }

    // Проверка на несовпадение типов и вставка конвертации Int2Float
    private void checkForTypeMismatches(SyntaxNode node) {
        if (node == null) return;

        // Если операнды разных типов, добавляем конвертацию типов
        if (node.getLeft() != null && node.getRight() != null) {
            Token leftToken = node.getLeft().getToken();
            Token rightToken = node.getRight().getToken();

            String leftType = getVariableType(leftToken);
            String rightType = getVariableType(rightToken);

            // Если левый операнд типа "целый", а правый - "вещественный", то левый операнд преобразуем
            if (leftType.equals("целый") && rightType.equals("вещественный")) {
                node.setLeft(new SyntaxNode(new Token(TokenType.OPERATOR, "Int2Float"), node.getLeft(), null));
            }
            // Если правый операнд типа "целый", а левый - "вещественный", то правый операнд преобразуем
            else if (leftType.equals("вещественный") && rightType.equals("целый")) {
                node.setRight(new SyntaxNode(new Token(TokenType.OPERATOR, "Int2Float"), node.getRight(), null));
            }
        }

        // Рекурсивный обход для левого и правого поддерева
        checkForTypeMismatches(node.getLeft());
        checkForTypeMismatches(node.getRight());
    }



    // Получение типа переменной (или числа)
    private String getVariableType(Token token) {
        // Проверка на тип токена
        if (token.getType() == TokenType.ID) {
            int symbolId = Integer.parseInt(token.getValue().toString()); // Токен содержит ID переменной
            SymbolTable symbolTable = new SymbolTable();
            VariableType type = symbolTable.getType(symbolId); // Получаем тип из таблицы символов
            return type != null ? type : "целый";  // Если тип не найден, возвращаем "целый" по умолчанию
        } else if (token.getType() == TokenType.INT) {
            return "целый";  // Тип числа (int)
        } else if (token.getType() == TokenType.FLOAT) {
            return "вещественный";  // Тип числа (float)
        } else {
            return "неизвестный";  // Для других типов токенов
        }
    }*/

    /**
     * Метод семантического анализа и получения модифицированного синтаксического дерева.
     *
     * @param syntaxTree синтаксическое дерево.
     * @param symbolTable таблица символов.
     * @return модифицированное синтаксическое дерево.
     */
    public static SemanticTree analyze(SyntaxNode syntaxTree, SymbolTable symbolTable) throws IllegalArgumentException {
        return getSemanticTree(syntaxTree, symbolTable);
    }

    /**
     *
     * @param syntaxTree
     * @param symbolTable
     * @return
     */
    public static SemanticTree getSemanticTree(SyntaxNode syntaxTree, SymbolTable symbolTable) throws IllegalArgumentException {
        return new SemanticTree(syntaxTree, symbolTable);
    }



}


