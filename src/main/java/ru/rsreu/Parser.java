package ru.rsreu;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public SyntaxNode parse() {
        SyntaxNode expressionTree = parseExpression();
        if (position < tokens.size()) {
            throw new IllegalArgumentException("Синтаксическая ошибка! Лишние токены после позиции " + (position + 1));
        }
        return expressionTree;
    }

    private SyntaxNode parseExpression() {
        SyntaxNode node = parseTerm(); // Парсинг первой части выражения
        while (position < tokens.size()) {
            Token token = tokens.get(position);
            if (token.getType() == TokenType.OPERATOR && (token.getValue().equals("+") || token.getValue().equals("-"))) {
                position++;
                node = new SyntaxNode(token, node, parseTerm()); // Обработка "+" и "-"
            } else {
                break;
            }
        }
        return node;
    }

    private SyntaxNode parseTerm() {
        SyntaxNode node = parseFactor(); // Начинаем с фактора
        while (position < tokens.size()) {
            Token token = tokens.get(position);
            if (token.getType() == TokenType.OPERATOR && (token.getValue().equals("*") || token.getValue().equals("/"))) {
                position++;
                node = new SyntaxNode(token, node, parseFactor()); // Обработка "*" и "/"
            } else {
                break;
            }
        }
        return node;
    }

    private SyntaxNode parseFactor() {
        Token token = tokens.get(position);
        SyntaxNode node;

        if (token.getType() == TokenType.ID || token.getType() == TokenType.INT || token.getType() == TokenType.FLOAT) {
            node = new SyntaxNode(token);
            position++;
            checkOperandSequence(); // Проверка на ошибки последовательности операндов
        } else if (token.getType() == TokenType.PARENTHESIS && token.getValue().equals("(")) {
            int openingPosition = position;
            position++;

            node = parseExpression();

            if (position >= tokens.size() || !tokens.get(position).getValue().equals(")")) {
                throw new IllegalArgumentException("Синтаксическая ошибка! У открывающей скобки <(> на позиции " + (openingPosition + 1) + " отсутствует закрывающая скобка.");
            }
            position++;
        } else {
            throw new IllegalArgumentException("Синтаксическая ошибка! Ожидался операнд на позиции " + (position + 1));
        }
        return node;
    }

    private void checkOperandSequence() {
        if (position < tokens.size()) {
            Token nextToken = tokens.get(position);
            if (nextToken.getType() == TokenType.ID || nextToken.getType() == TokenType.INT || nextToken.getType() == TokenType.FLOAT) {
                throw new IllegalArgumentException("Синтаксическая ошибка! За операндом " + tokens.get(position - 1).toString() + " на позиции " + (position) + " следует еще один операнд.");
            }
        }
    }
}
