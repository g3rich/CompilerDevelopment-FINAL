package ru.rsreu;

import java.util.Objects;

public class SemanticTree {
    private SyntaxNode root; // Корень модифицированного дерева
    private final SyntaxNode syntaxTree;
    private final SymbolTable symbolTable;

    public SemanticTree(SyntaxNode syntaxTree, SymbolTable symbolTable) throws IllegalArgumentException {
        this.syntaxTree = syntaxTree;
        this.symbolTable = symbolTable;
        this.root = buildTree(syntaxTree);
    }

    public SyntaxNode getRoot() {
        return root;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    private SyntaxNode buildTree(SyntaxNode node) throws IllegalArgumentException {
        Token token = node.getToken();

        if (node.isEndNode()) {
            return new SyntaxNode(token);
        }

        /*if (node.getLeft() == null && node.getRight() == null) {
            // Конечный узел
            return new SyntaxNode(token);
        }*/

        SyntaxNode newNode = new SyntaxNode(token);
        newNode.left= buildTree(node.left);
        newNode.right = buildTree(node.right);

        if (token.getType() == TokenType.OPERATOR) {
            // Проверка деления на 0
            /*if ("/".equals(token.getValue())) {
                Token rightToken = node.getRight().getToken();
                if ("int".equals(getType(node.getRight())) && "0".equals(rightToken.getValue().toString())) {
                    throw new IllegalArgumentException("Семантическая ошибка! Деление на 0.");
                }
            }*/
            Token rightToken = node.right.getToken();
            if (Objects.equals((String) token.getValue(), "/")){
                if (rightToken.getType() == TokenType.INT) {
                    if(Integer.parseInt((String)rightToken.getValue()) == 0){
                        throw new IllegalArgumentException("Семантическая ошибка! Обнаружено деление на 0.");
                    }
                //if (rightToken.getType() != TokenType.OPERATOR && rightToken.getType() != TokenType.PARENTHESIS && rightToken.getType() != TokenType.FUNCTION && rightToken.getType() != TokenType.EQUAL) {
                } else {
                    if (rightToken.getType() == TokenType.FLOAT){
                        if(Float.parseFloat((String)rightToken.getValue()) == 0.0){
                            throw new IllegalArgumentException("Семантическая ошибка! Обнаружено деление на 0.");
                        }
                    }
                }
            }

            SyntaxNode leftNode = node.left;
            SyntaxNode rightNode = node.right;

            // Приведение типов
            VariableType leftType = getType(leftNode);
            VariableType rightType = getType(rightNode);

            if (leftType != rightType && rightType != null && leftType != null) {
                if (leftType == VariableType.INT) {
                    newNode.left = new SyntaxNode(new Token(TokenType.FUNCTION, "Int2Float", 0));
                    newNode.left.left = node.left;
                } else {
                    newNode.right = new SyntaxNode(new Token(TokenType.FUNCTION, "Int2Float", 0));
                    newNode.right.right = node.right;
                }
            }

            /*if (!leftType.equals(rightType)) {
                if ("int".equals(leftType)) {
                    SyntaxNode conversionNode = new SyntaxNode(new Token(TokenType.ID, "Int2Float"));
                    conversionNode.setLeft(newNode.getLeft());
                    newNode.setLeft(conversionNode);
                } else if ("int".equals(rightType)) {
                    SyntaxNode conversionNode = new SyntaxNode(new Token(TokenType.ID, "Int2Float"));
                    conversionNode.setLeft(newNode.getRight());
                    newNode.setRight(conversionNode);
                }
            }*/
        }

        return newNode;
    }

    private VariableType getType(SyntaxNode node) throws IllegalArgumentException {
        /*if (node == null) {
            return null;
        }*/

        Token token = node.getToken();

        switch (token.getType()) {
            case ID -> {
                // Получаем тип переменной из таблицы символов
                //Symbol symbol = symbolTable.getSymbol((int) token.getValue());
                Symbol symbol = symbolTable.getSymbol(Integer.parseInt((String) token.getValue()));
                return symbol.getType();
                //return symbolTable.getType((int) token.getType());
            }
            case INT -> {
                return VariableType.INT;
            }
            case FLOAT -> {
                return VariableType.FLOAT;
            }
            case OPERATOR -> {
                if (getType(node.left) != getType(node.right)) {
                    return  VariableType.FLOAT;
                } else {
                    return getType(node.left);
                }
            }
        }
        return null;
    }

    public String printTree(SyntaxNode node, int level) {
        if (node == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        if (level != 0) {
            result.append("\t".repeat(Math.max(0, level - 1))).append("|---");
        }

        if (node.getToken().getType() == TokenType.ID) {
            result.append("<id,").append(node.getToken().getValue()).append(">\n");
        }
        else {
            result.append("<").append(node.getToken().getValue()).append(">\n");
        }

        result.append(printTree(node.left, level + 1));
        result.append(printTree(node.right, level + 1));
        /*result.append("\t".repeat(Math.max(0, level - 1)));
        if (level != 0) {
            result.append("|---");
        }

        result.append(node.getToken()).append("\n");

        result.append(printTree(node.getLeft(), level + 1));
        result.append(printTree(node.getRight(), level + 1));*/

        return result.toString();
    }
}

