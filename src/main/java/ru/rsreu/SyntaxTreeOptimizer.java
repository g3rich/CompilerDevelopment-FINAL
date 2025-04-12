package ru.rsreu;

import java.util.List;
import java.util.Objects;

public class SyntaxTreeOptimizer {

    public static void optimizeTree(SemanticTree tree) {
        // Рекурсивный вызов с корня дерева
        optimizeNode(tree != null ? tree.getRoot() : null);
        //return tree;
    }

    private static void optimizeNode(SyntaxNode node) {
        // Если узел null, сразу выходим
        if (node == null) return;

        // Рекурсивно обрабатываем дочерние узлы
        if(node.left != null || node.right != null) {
            if (node.left != null) {
                optimizeNode(node.left);
            }
            if (node.right != null) {
                optimizeNode(node.right);
            }
        }

        // Оптимизация текущего узла
        optimizeExpression(node);
    }

    private static void optimizeExpression(SyntaxNode node) {
        // 1. Проверка и оптимизация выражений с константами
        if ((node.getToken().getType() == TokenType.OPERATOR) && node.left != null && node.right != null) {
            SyntaxNode leftNode = node.left;
            SyntaxNode rightNode = node.right;

            // Если оба операнда константы
            if ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                    (rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT)) {
                float leftValue = Float.parseFloat(leftNode.getToken().getValue().toString());
                float rightValue = Float.parseFloat(rightNode.getToken().getValue().toString());
                float result = 0;
                TokenType resType = TokenType.INT;

                // Определение типа результата операции
                if (leftNode.getToken().getType() == TokenType.FLOAT ||
                        rightNode.getToken().getType() == TokenType.FLOAT) {
                    resType = TokenType.FLOAT;
                }

                // Операции
                switch (node.getToken().getType()) {
                    case OPERATOR:
                        if (Objects.equals((String) node.getToken().getValue(), "+")) {
                            result = leftValue + rightValue;
                            break;
                        } else if (Objects.equals((String) node.getToken().getValue(), "-")) {
                            result = leftValue - rightValue;
                            break;
                        } else if (Objects.equals((String) node.getToken().getValue(), "*")) {
                            result = leftValue * rightValue;
                            break;
                        } else if (Objects.equals((String) node.getToken().getValue(), "/")) {
                            if (rightValue != 0) {
                                result = leftValue / rightValue;
                                resType = TokenType.FLOAT;
                            } else {
                                throw new IllegalArgumentException("Семантическая ошибка! Обнаружено деление на 0.");
                            }
                            break;
                        }
                    default:
                        break;
                }

                // Устанавливаем оптимизированный токен
                if (resType == TokenType.FLOAT) {
                    node.setToken(new Token(TokenType.FLOAT, result, 0));
                } else {
                    node.setToken(new Token(TokenType.INT, (int) result, 0));
                }

                // Убираем дочерние узлы
                node.setLeft(null);
                node.setRight(null);
            }
        }

        // 2. Оптимизация преобразований int2float
        if (node.getToken().getType() == TokenType.FUNCTION &&
                ((node.left != null && node.right == null) || (node.left == null && node.right != null))) {
            SyntaxNode childNode = null;
            if (node.left != null){
                childNode = node.left;
            } else {
                childNode = node.right;
            }
            if (childNode.getToken().getType() == TokenType.INT) {
                float floatValue = Float.parseFloat(childNode.getToken().getValue().toString());
                node.setToken(new Token(TokenType.FLOAT, floatValue, 0));
                if (childNode.left != null){
                    node.setLeft(childNode.left);
                }
                if (childNode.right != null){
                    node.setRight(childNode.right);
                }
            }
        }

        // 3. Оптимизация тривиальных операций
        optimizeTrivialOperations(node);
    }

    private static void optimizeTrivialOperations(SyntaxNode node) {
        if (node.left == null || node.right == null) return;

        SyntaxNode leftNode = node.left;
        SyntaxNode rightNode = node.right;

        switch (node.getToken().getType()) {
            case OPERATOR:
                //if (Objects.equals((String) node.getToken().getValue(), "+")) {}
                if ((Objects.equals((String) node.getToken().getValue(), "-") && (node.left != null && node.right != null)) ||
                        (Objects.equals((String) node.getToken().getValue(), "+") && (node.left != null && node.right != null))) {
                    if ((rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(rightNode.getToken().getValue().toString()) == 0) {
                        node.setToken(leftNode.getToken());
                        if (leftNode.left != null){
                            node.setLeft(leftNode.left);
                        }
                        if (leftNode.right != null){
                            node.setRight(leftNode.right);
                        }
                    } else if ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(leftNode.getToken().getValue().toString()) == 0) {
                        node.setToken(rightNode.getToken());
                        if (rightNode.left != null){
                            node.setLeft(rightNode.left);
                        }
                        if (rightNode.right != null){
                            node.setRight(rightNode.right);
                        }
                    }
                    break;
                } else if (Objects.equals((String) node.getToken().getValue(), "*")) {
                    /*if (((rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(rightNode.getToken().getValue().toString()) == 0) ||
                            ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                                    Float.parseFloat(leftNode.getToken().getValue().toString()) == 0)) {
                        node.setToken(new Token(TokenType.INT, 0));
                        node.setLeft(null);
                        node.setRight(null);
                    } else */if ((rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(rightNode.getToken().getValue().toString()) == 0){
                        //node.setToken(new Token(TokenType.INT, 0));
                        node.setToken(rightNode.getToken());
                        node.setLeft(null);
                        node.setRight(null);
                    } else if ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(leftNode.getToken().getValue().toString()) == 0) {
                        node.setToken(leftNode.getToken());
                        node.setLeft(null);
                        node.setRight(null);
                    }else if ((rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(rightNode.getToken().getValue().toString()) == 1) {
                        //System.out.println(Float.parseFloat(rightNode.getToken().getValue().toString()));
                        node.setToken(leftNode.getToken());
                        node.setLeft(leftNode.left);
                        node.setRight(leftNode.right);
                    } else if ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(leftNode.getToken().getValue().toString()) == 1) {
                        //System.out.println(Float.parseFloat(leftNode.getToken().getValue().toString()));
                        node.setToken(rightNode.getToken());
                        node.setLeft(rightNode.left);
                        node.setRight(rightNode.right);
                    }
                    break;
                } else if (Objects.equals((String) node.getToken().getValue(), "/")) {
                    if ((leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(leftNode.getToken().getValue().toString()) == 0) {
                        //System.out.println("div");
                        node.setToken(leftNode.getToken());
                        //node.setLeft(rightNode.left);
                        //node.setRight(rightNode.right);
                        node.setLeft(null);
                        node.setRight(null);
                    } else if ((rightNode.getToken().getType() == TokenType.FLOAT || rightNode.getToken().getType() == TokenType.INT) &&
                            Float.parseFloat(rightNode.getToken().getValue().toString()) == 1) {
                        //System.out.println("div");
                        node.setToken(leftNode.getToken());
                        //node.setLeft(rightNode.left);
                        //node.setRight(rightNode.right);
                        node.setLeft(leftNode.left);
                        node.setRight(leftNode.right);
                    }

                    /*else if (leftNode.getToken().getType() == TokenType.FLOAT || leftNode.getToken().getType() == TokenType.INT &&
                    Float.parseFloat(leftNode.getToken().getValue().toString()) == 0.0){
                        node.setToken(leftNode.getToken());
                        node.setLeft(leftNode.left);
                        node.setRight(leftNode.right);
                    }*/
                    break;
                }
            default:
                break;
        }
    }

    /*private static boolean isOperator(Token token) {
        return token.getType() == LexicalTypesEnum.Addition ||
                token.getType() == LexicalTypesEnum.Subtraction ||
                token.getType() == LexicalTypesEnum.Multiplication ||
                token.getType() == LexicalTypesEnum.Division;
    }

    private static boolean isConstant(Token token) {
        return token.getType() == LexicalTypesEnum.IntegerConstant ||
                token.getType() == LexicalTypesEnum.RealConstant;
    }*/
}
