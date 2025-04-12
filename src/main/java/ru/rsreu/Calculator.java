package ru.rsreu;

import java.util.List;
import java.util.Map;

public class Calculator {
    private SymbolTable symbolsTable;
    private List<Instruction> instructions;

    public Calculator(SymbolTable symbolsTable, List<Instruction> instructions) {
        this.symbolsTable = symbolsTable;
        this.instructions = instructions;
    }

    public void executeInstructions() {
        for (Instruction instruction : instructions) {
            Token resultToken = instruction.getResToken();

            if (instruction.getTokens().size() == 2) { // Бинарные операции
                Token operand1 = instruction.getTokens().get(0);
                Token operand2 = instruction.getTokens().get(1);

                float value1 = (float) getValue(operand1);
                float value2 = (float) getValue(operand2);

                TokenType type1 = getType(operand1);
                TokenType type2 = getType(operand2);

                float result = 0;

                switch (instruction.getManual()) {
                    case add:
                        result = value1 + value2;
                        break;
                    case sub:
                        result = value1 - value2;
                        break;
                    case mul:
                        result = value1 * value2;
                        break;
                    case div:
                        result = value1 / value2;
                        break;
                    default:
                        System.out.println("Неизвестная операция");
                        break;
                }

                TokenType resultType = determineResultType(type1, type2);

                VariableType resultSymbolType;
                if (resultType == TokenType.INT) {
                    resultSymbolType = VariableType.INT;
                } else {
                    resultSymbolType = VariableType.FLOAT;
                }
                symbolsTable.getSymbol(resultToken.getId()).setValue(result);
                symbolsTable.getSymbol(resultToken.getId()).setType(resultSymbolType);

                System.out.printf("Результат операции %s: %.4f; тип результата: %s%n",
                        instruction.getManual(), result, resultType);
            } else if (instruction.getTokens().size() == 1) { // Унарные операции
                Token operand = instruction.getTokens().get(0);

                float value = getValue(operand);
                TokenType operandType = getType(operand);

                float result = 0;

                switch (instruction.getManual()) {
                    case i2f: // Преобразование целого в вещественное
                        result = value;
                        operandType = TokenType.FLOAT;
                        break;
                    default:
                        System.out.println("Неизвестная унарная операция");
                        break;
                }

                /*TokenType resultType = operandType == TokenType.INT
                        ? TokenType.FLOAT
                        : operandType;*/
                VariableType resultType;
                if (operandType == TokenType.INT) {
                    resultType = VariableType.INT;
                } else {
                    resultType = VariableType.FLOAT;
                }

                symbolsTable.getSymbol(resultToken.getId()).setValue(result);
                symbolsTable.getSymbol(resultToken.getId()).setType(resultType);

                System.out.printf("Результат операции %s: %.4f; тип результата: %s%n",
                        instruction.getManual(), result, resultType);
            }
        }

        if (!instructions.isEmpty()) {
            Instruction lastInstruction = instructions.get(instructions.size() - 1);
            Token result = lastInstruction.getResToken();

            System.out.printf("Итого: %.4f%n",
                    (float) symbolsTable.getSymbol(result.getId()).getValue());
        }
    }

    private float getValue(Token token) {
        if (token.getType() == TokenType.INT ||
                token.getType() == TokenType.FLOAT) {
            if (token.getValue().toString().contains("#T")){
                return Float.parseFloat(symbolsTable.getSymbol(token.getId()).getValue().toString());
            }
            return  Float.parseFloat((String) token.getValue());/*((Number) token.getValue()).doubleValue();*/
        } else {
            Symbol symbol = symbolsTable.getSymbol(token.getId());
            if (symbol.getType() == VariableType.INT ||
                    symbol.getType() == VariableType.FLOAT) {
                return Float.parseFloat(symbol.getValue().toString());
            } else {
                return Float.parseFloat(symbol.getValue().toString());
            }
        }
    }

    private TokenType getType(Token token) {
        if (token.getType() == TokenType.INT) {
            return TokenType.INT;
        } else if (token.getType() == TokenType.FLOAT) {
            return TokenType.INT;
        } else {
            VariableType varType =  symbolsTable.getSymbol(token.getId()).getType();
            if (varType == VariableType.INT) {
                return TokenType.INT;
            } else if (varType == VariableType.FLOAT) {
                return TokenType.FLOAT;
            }
            return token.getType();
            //return symbolsTable.getSymbol(token.getId()).getType();
        }
    }

    private TokenType determineResultType(TokenType type1, TokenType type2) {
        if (type1 == TokenType.INT && type2 == TokenType.INT) {
            return TokenType.INT;
        } else {
            return TokenType.FLOAT;
        }
    }
}
