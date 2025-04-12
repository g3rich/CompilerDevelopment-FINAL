package ru.rsreu;

public class SyntaxNode { Token token;
    public SyntaxNode left;
    public SyntaxNode right;

    // Конструктор с токеном и детьми
    public SyntaxNode(Token token, SyntaxNode left, SyntaxNode right) {
        this.token = token;
        this.left = left;
        this.right = right;
    }

    // Конструктор с токеном
    public SyntaxNode(Token token) {
        this(token, null, null);
    }

    // Геттеры
    public Token getToken() {
        return token;
    }

    public SyntaxNode getLeft() {
        return left;
    }

    public SyntaxNode getRight() {
        return right;
    }

    // Сеттеры для изменения детей
    public void setLeft(SyntaxNode left) {
        this.left = left;
    }

    public void setRight(SyntaxNode right) {
        this.right = right;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public boolean isEndNode() {return this.right == null && this.left == null;}

    /*@Override
    public String toString() {
        return token.toString();
    }*/
}
