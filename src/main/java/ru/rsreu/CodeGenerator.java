package ru.rsreu;

import java.io.IOException;
import java.util.*;

//import static com.sun.org.apache.xpath.internal.compiler.Token.contains;

public class CodeGenerator {

    private final SemanticTree semanticTree;
    private SymbolTable symbolTable;
    private final FileWriter fileWriter;
    //private StringBuilder code; // Хранит сгенерированный трехадресный код
    private int tempVarCounter; // Счетчик для временных переменных
    private List<Instruction> instructions;
    private List<Token> postfixTokens;
    private Deque<Token> temporaryVarToken;
    private boolean optimizeFactor;

    public CodeGenerator(SemanticTree semanticTree, SymbolTable symbolTable, boolean optimizeFactor) {
        this.semanticTree = semanticTree;
        this.symbolTable = symbolTable;
        this.fileWriter = new FileWriter(); // Используем FileWriter для записи в файл
        //this.code = new StringBuilder();
        this.tempVarCounter = 1; // Начинаем с #T1
        this.instructions = new ArrayList<>();
        this.postfixTokens = new ArrayList<>();
        this.temporaryVarToken = new ArrayDeque<>();
        this.optimizeFactor = optimizeFactor;
    }

    public List<Instruction> getInstructions() {
        return this.instructions;
    }

    // Метод для генерации трехадресного кода
    public void generateCode() throws IOException {
        generateCodeRecursively(semanticTree.getRoot());
    }

    // Рекурсивная генерация трехадресного кода
    private Token generateCodeRecursively(SyntaxNode node) {
        //System.out.println("opR_all " + node.getToken().getType().toString());
        /*if (node == null) {
            return;
        }

        Token token = node.getToken();

        // Проверка, что токен существует, иначе избежать NullPointerException
        if (token == null) {
            return;
        }

        if (token.getType() == TokenType.OPERATOR || token.getType() == TokenType.FUNCTION) {
            String operator = (String) token.getValue();

            // Генерация кода для операций с двумя операндами
            if (operator.equals("+")) {
                String operand1 = getOperand(node.left);  // Получаем операнд1
                String operand2 = getOperand(node.right); // Получаем операнд2
                String temp = generateTemporaryVariable();
                code.append("add ").append(temp).append(" ").append(operand1).append(" ").append(operand2).append("\n");
            }
            else if (operator.equals("*")) {
                String operand1 = getOperand(node.left);  // Получаем операнд1
                String operand2 = getOperand(node.right); // Получаем операнд2
                String temp = generateTemporaryVariable();
                code.append("mul ").append(temp).append(" ").append(operand1).append(" ").append(operand2).append("\n");
            }
            else if (operator.equals("Int2Float")) {
                String operand = getOperand(node.left); // Получаем операнд для преобразования
                String temp = generateTemporaryVariable();
                code.append("i2f ").append(temp).append(" ").append(operand).append("\n");
            }
        }

        // Рекурсивно обрабатываем левое и правое поддерево
        generateCodeRecursively(node.left);
        generateCodeRecursively(node.right);*/
        if (node == null) {
            return null;
        }

        // Если узел является операцией с двумя операндами
        if ((node.getToken().getType() == TokenType.OPERATOR) && node.left != null && node.right != null) {
            // Рекурсивно обрабатываем левый и правый операнды
            //System.out.println("opL" + node.left.getToken().getType().toString());
            //System.out.println("opR" + node.right.getToken().getType().toString());
            Token leftResult = generateCodeRecursively(node.left);
            Token rightResult = generateCodeRecursively(node.right);

            // Генерируем временную переменную для текущей операции
            TokenType type = TokenType.INT; // Стандартный тип - целое число
            //System.out.println("type op " + type.toString());
            if (node.left.getToken().getType() == TokenType.INT && node.right.getToken().getType() == TokenType.INT) {
                //System.out.println("1");
                type = TokenType.INT;
            } else if (node.left.getToken().getType() == TokenType.FLOAT || node.right.getToken().getType() == TokenType.FLOAT) {
                //System.out.println("2");
                type = TokenType.FLOAT;
            } else if (node.left.getToken().getType() == TokenType.ID || node.right.getToken().getType() == TokenType.ID) {
                //System.out.println("3");
                if (node.left.getToken().getType() == TokenType.ID) {
                    int id = Integer.parseInt((String)node.left.getToken().getValue());
                    Symbol symbol = symbolTable.getSymbol(id);
                    if (symbol.getType() == VariableType.FLOAT) {
                        type = TokenType.FLOAT;
                    }
                }
                if (node.right.getToken().getType() == TokenType.ID) {
                    int id = Integer.parseInt((String)node.right.getToken().getValue());
                    Symbol symbol = symbolTable.getSymbol(id);
                    if (symbol.getType() == VariableType.FLOAT) {
                        type = TokenType.FLOAT;
                    }
                }
            }

            /*Token tempVar = null;
            if (temporaryVarToken.size() > 0) {
                Token temporaryVar = temporaryVarToken.pollLast();
                System.out.println("tempVAROPER " + temporaryVar);

                if (temporaryVar.toString().contains("#T")){
                    System.out.println("tempVAROPER contains " + temporaryVar);
                    temporaryVar.setType(type);
                    tempVar = temporaryVar;
                }
            } else {
                String tempVarName = "#T" + tempVarCounter++;
                // Добавляем временную переменную в таблицу символов
                int idTempVar = symbolTable.addSymbol(tempVarName, type.toString());

                // Создаём токен для временной переменной
                tempVar = new Token(TokenType.ID, symbolTable.getSymbol(idTempVar));
            }*/
            //System.out.println("now operator");
            Token tempVar = getTemporaryVarToken(type);

            // Добавляем временную переменную в таблицу символов
            //int idTempVar = symbolTable.addSymbol(tempVarName, type.toString());

            // Создаём токен для временной переменной
            //Token tempVar = new Token(TokenType.ID, symbolTable.getSymbol(idTempVar));

            List<Token> immutable_list = new ArrayList<Token>();
            immutable_list.add(leftResult);
            immutable_list.add(rightResult);
            //System.out.println(immutable_list.get(0).toString());
            //System.out.println(immutable_list.get(1).toString());
            // Генерация инструкции (например, сложение или умножение)
            Instruction instruction = new Instruction(node.getToken(), tempVar, immutable_list, symbolTable);
            instructions.add(instruction);

            if (optimizeFactor){
                optimizeResult(leftResult);
                optimizeResult(rightResult);
            }

            return tempVar;  // Возвращаем токен временной переменной
            //return operatorGeneration(node);
        }

        // Если узел является преобразованием типа (например, int2float)
        else if ((node.getToken().getType() == TokenType.FUNCTION)/* && node.left != null && node.right == null*/) {

            //System.out.println("opRF " + node.getToken().getType().toString());
            Token operandResult = null;
            if (node.left != null){
                operandResult = generateCodeRecursively(node.left);
            } else {
                operandResult = generateCodeRecursively(node.right);
            }


            // Генерация временной переменной для преобразования
            Token tempVar = getTemporaryVarToken(TokenType.FLOAT);
            /*String tempVarName = "#T" + tempVarCounter++;
            int idTempVar = symbolTable.addSymbol(tempVarName, VariableType.FLOAT.toString());

            Token tempVar = new Token(TokenType.ID, symbolTable.getSymbol(idTempVar));*/

            /*Token tempVar = null;
            if (temporaryVarToken.size() > 0) {
                Token temporaryVar = temporaryVarToken.pollLast();

                if (temporaryVar.toString().contains("#T")){
                    temporaryVar.setType(type);
                    tempVar = temporaryVar;
                }
            } else {
                // Добавляем временную переменную в таблицу символов
                int idTempVar = symbolTable.addSymbol(tempVarName, type.toString());

                // Создаём токен для временной переменной
                tempVar = new Token(TokenType.ID, symbolTable.getSymbol(idTempVar));
            }*/

            List<Token> tokenList = new ArrayList<Token>();
            tokenList.add(operandResult);
            // Генерация инструкции для преобразования
            Instruction instruction = new Instruction(node.getToken(), tempVar, tokenList, symbolTable);

            if(optimizeFactor){
                optimizeResult(operandResult);
            }
            instructions.add(instruction);

            return tempVar;
        }

        // Если узел является идентификатором или константой
        else if (node.getToken().getType() == TokenType.ID ||
                node.getToken().getType() == TokenType.INT ||
                node.getToken().getType() == TokenType.FLOAT) {
            return node.getToken();  // Возвращаем сам токен (переменную или константу)
        }

        return null;
    }

    private Token operatorGeneration(SyntaxNode node){
        Token leftResult = generateCodeRecursively(node.left);
        Token rightResult = generateCodeRecursively(node.right);

        // Генерируем временную переменную для текущей операции
        //String tempVarName = "#T" + tempVarCounter++;
        TokenType type = TokenType.INT; // Стандартный тип - целое число
        //System.out.println((node.getToken().getType().toString()));
        if (node.getToken().getType() == TokenType.INT) {
            type = TokenType.INT;
        } else if (node.getToken().getType() == TokenType.FLOAT) {
            type = TokenType.FLOAT;
        }
        Token tempVar = getTemporaryVarToken(type);

        List<Token> immutable_list = new ArrayList<Token>();
        immutable_list.add(leftResult);
        immutable_list.add(rightResult);
        //System.out.println(immutable_list.get(0).toString());
        //System.out.println(immutable_list.get(1).toString());

        // Генерация инструкции (сложение или умножение)
        Instruction instruction = new Instruction(node.getToken(), tempVar, immutable_list, symbolTable);
        instructions.add(instruction);

        if (optimizeFactor){
            optimizeResult(leftResult);
            optimizeResult(rightResult);
        }

        return tempVar;
    }

    private Token getTemporaryVarToken(TokenType type) {
        //System.out.println("getTEMPVAR " + type.toString());
        if (temporaryVarToken.size() > 0) {
            Token temporaryVar = temporaryVarToken.pollLast();
            //System.out.println("temporaryVarToken " + temporaryVar.toString());

            if (temporaryVar.toString().contains("#T")){
                temporaryVar.setType(type);
                //System.out.println("temporaryVarType " + temporaryVar.getType().toString());
                return temporaryVar;
            }
        }

        //System.out.println("getTEMPVAR NEW " + type.toString());
        String temporaryVarName = "#T" + tempVarCounter++;
        int idTemporaryVar = symbolTable.addSymbol(temporaryVarName, type.toString());
        //System.out.println("fact type " + symbolTable.getSymbol(idTemporaryVar).getType() + " fact type");
        Token temporaryVar = new Token(TokenType.ID, symbolTable.getSymbol(idTemporaryVar), idTemporaryVar);

        return temporaryVar;
    }

    private void optimizeResult(Token temporaryVar){
        //System.out.println("optimResult " + temporaryVar.toString());
        if (temporaryVar != null && temporaryVar.toString().contains("#T")){
            //System.out.println("optimResult IF " + temporaryVar.toString());
            temporaryVarToken.addLast(temporaryVar);
        }
    }

    // Генерация уникальной временной переменной
    /*private String generateTemporaryVariable() {
        return "#T" + tempCounter++;
    }*/

    // Получение операнда для трехадресного кода
    private String getOperand(SyntaxNode node) {
        // Если узел null, возвращаем пустую строку
        if (node == null) {
            return "";
        }

        Token token = node.getToken();

        if (token.getType() == TokenType.ID) {
            return "<id," + token.getValue() + ">";
        } else if (token.getType() == TokenType.INT || token.getType() == TokenType.FLOAT) {
            return token.getValue().toString();
        }
        return ""; // Возвращаем пустое значение, если операнд не найден
    }

    public List<Token> generatePostfixNotation()
    {
        var postfixBuilder = new StringBuilder();
        traverseTreeForPostfix(semanticTree.getRoot());
        return this.postfixTokens;
    }

    private void traverseTreeForPostfix(SyntaxNode node)
    {
        if (node == null)
            return;

        // Если узел является операцией
        if (node.getToken().getType() == TokenType.OPERATOR)
        {
            // Если узел бинарный (двухоперандный)
            if (node.left != null && node.right != null)
            {
                traverseTreeForPostfix(node.left); // Левый операнд
                traverseTreeForPostfix(node.right); // Правый операнд
                //if (node.Token.Type == LexicalTypesEnum.Coercion)
                //{
                //  postfixBuilder.Append($"<{GenerationInstructionsEnum.i2f}>");
                //} else
                //{
                //  postfixBuilder.Append($"<{node.Token.Value}>");
                //}
                postfixTokens.add(node.getToken());
            }
            // Если узел унарный (int2float)
            else if (node.left != null)
            {
                traverseTreeForPostfix(node.left);
                postfixTokens.add(node.getToken());
            }
            else if (node.right != null){
                traverseTreeForPostfix(node.right);
                postfixTokens.add(node.getToken());
            }
        }
        else if (node.getToken().getType() == TokenType.ID ||
                node.getToken().getType() == TokenType.INT ||
                node.getToken().getType() == TokenType.FLOAT)
        {
            //System.out.println(node.getToken().getType().toString());
            //System.out.println(node.getToken().getValue().toString());
            postfixTokens.add(node.getToken());
        } else if (node.getToken().getType() == TokenType.FUNCTION) {
            if (node.left != null)
            {
                traverseTreeForPostfix(node.left);
                postfixTokens.add(node.getToken());
            }
            else if (node.right != null){
                traverseTreeForPostfix(node.right);
                postfixTokens.add(node.getToken());
            }
        }
    }

    // Генерация таблицы символов
    public void generateSymbolTable(String fileName, SymbolTable generatedSymbolTable) {
        symbolTable = generatedSymbolTable;
        StringBuilder symbolTableContent = new StringBuilder();
        Map<Integer, Symbol> symbols = symbolTable.getSymbols();
        for (Map.Entry<Integer, Symbol> entry : symbols.entrySet()) {
            Symbol symbol = entry.getValue();
            symbolTableContent.append("<id,").append(entry.getKey()).append("> - ")
                    .append(symbol.getName()).append(", ")
                    .append(symbol.getType()).append("\n");
        }

        try {
            fileWriter.writeFile(fileName, symbolTableContent.toString()); // Записываем таблицу символов
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatePostfixSymbolTable(String fileName) {

    }
}
