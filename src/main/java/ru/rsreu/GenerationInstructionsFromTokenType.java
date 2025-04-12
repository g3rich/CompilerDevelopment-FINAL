package ru.rsreu;

public class GenerationInstructionsFromTokenType {

    /**
     * Метод для получения команды из типа токена.
     *
     * @param token токен
     * @return соответствующая инструкция типа GenerationInstructionsEnum
     */
    public static GenerationInstructionsEnum toGetInstruction(Token token) {
        //System.out.println(token.getValue());
        //System.out.println(token.getType());
        switch (token.getType()) {
            case OPERATOR:
                if (token.getValue().equals("-")){
                    return GenerationInstructionsEnum.sub;
                } else if (token.getValue().equals("+")){
                    return GenerationInstructionsEnum.add;
                } else if (token.getValue().equals("*")){
                    return GenerationInstructionsEnum.mul;
                } else if (token.getValue().equals("/")){
                    return GenerationInstructionsEnum.div;
                }
            case FUNCTION:
                return GenerationInstructionsEnum.i2f; // Например, для Int2Float.
            default:
                return GenerationInstructionsEnum.unknown; // Для неизвестного типа.
        }
    }
    public static Token toSetInstruction(GenerationInstructionsEnum type) {
        switch (type) {
            case add:
                return new Token(TokenType.OPERATOR, String.valueOf('+'), 0);
            case sub:
                return new Token(TokenType.OPERATOR, String.valueOf('-'), 0);
            case mul:
                return new Token(TokenType.OPERATOR, String.valueOf('*'), 0);
            case div:
                return new Token(TokenType.OPERATOR, String.valueOf('/'), 0);
            case i2f:
                return new Token(TokenType.FUNCTION, "Int2Float", 0);
            default:
                return new Token(TokenType.FUNCTION, "Unknown", 0);
        }
    }
}
