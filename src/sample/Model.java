package sample;


import java.lang.reflect.Array;
import java.util.*;

/* this class calculate math expression by 3 modules:
    1. isRightGrammar -check expression is correct
    2. convertToRPN - give expression to reverse polish notation
    3. calculate -  evaluates an expression by stack machine algorithm
 */


class Model {

    // list of available operators
    private final String OPERATORS = "+-*/><?:=!";
    // temporary stack that holds operators and brackets
    private Stack<String> stackOperations = new Stack<String>();
    // stack for holding expression converted to reversed polish notation
    private Stack<String> stackRPN = new Stack<String>();
    // stack for holding the calculations result
    private Stack<String> stackCalc = new Stack<String>();

    //calculate by stack machine algorithm
    String calculate(String expression) {
            // make some preparations
            ArrayList<String> tokens = parse(expression);
            convertToRPN(tokens);
            // reverse stack
            Collections.reverse(stackRPN);
            //System.out.println(Arrays.toString(stackRPN.toArray()));
            stackCalc.clear();
            while (!stackRPN.empty()) {
                String token = stackRPN.pop();
                if (isNumber(token)) {
                    stackCalc.push(token);
                } else if (isOperator(token)) {
                    double operand1 = Double.parseDouble(stackCalc.pop());
                    double operand2 = Double.parseDouble(stackCalc.pop());
                    switch (token) {
                        case "+":
                            stackCalc.push(String.valueOf(operand2 + operand1));
                            break;
                        case "-":
                            stackCalc.push(String.valueOf(operand2 - operand1));
                            break;
                        case "u-":
                            stackCalc.push(String.valueOf(operand2 - operand1));
                            break;
                        case "*":
                            stackCalc.push(String.valueOf(operand2 * operand1));
                            break;
                        case "/":
                            stackCalc.push(String.valueOf(operand2 / operand1));
                            break;
                        case ">":
                            if (operand2 > operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                        case "<":
                            if (operand2 < operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                        case "==":
                            if (operand2 == operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                        case "!=":
                            if (operand2 != operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                        case ">=":
                            if (operand2 >= operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                        case "<=":
                            if (operand2 <= operand1) getPartOfTernaryExpression(true);
                            else getPartOfTernaryExpression(false);
                            break;
                    }
                }
            }
            return stackCalc.pop();
    }

    // get reverse polish notation by shunting-yard algorithm
    private void convertToRPN(ArrayList<String> tokens) {
        // cleaning stacks
        stackOperations.clear();
        stackRPN.clear();
        // loop for handling each token - shunting-yard algorithm
        for (String token : tokens) {
            if (isNumber(token)) {
                stackRPN.push(token);
            } else if (isOperator(token)) {
                while (!stackOperations.empty()
                        && isOperator(stackOperations.lastElement())
                        && (getPrecedence(token)
                        <= getPrecedence(stackOperations.lastElement()))) {
                    stackRPN.push(stackOperations.pop());
                }
                stackOperations.push(token);
            } else if (isOpenBracket(token)) {
                stackOperations.push(token);
            } else if (isCloseBracket(token)) {
                while (!stackOperations.empty()
                        && !isOpenBracket(stackOperations.lastElement())) {
                    stackRPN.push(stackOperations.pop());
                }
                stackOperations.pop();
            } else {
                System.out.println("Unexpected item!");
                stackOperations.clear();
                stackRPN.clear();
                return;
            }
        }
        while (!stackOperations.empty()) {
            stackRPN.push(stackOperations.pop());
        }


    }


    // since the isRightGrammar complexity is O (n), and convertToRPN is O (n), we can afford to divide the algorithm into two passes
    // check input Grammar is correct
    private boolean isRightGrammar(String[] tokens) {
        int numberBrackets = 0;
        if (isCloseBracket(tokens[0])) return false;
        for (int i = 0; i < tokens.length - 1; i++) {
            String token = tokens[i];
            String nextToken = tokens[i + 1];
            if (isOpenBracket(token) || isCloseBracket(token) || isOperator(token) || isNumber(token)) {
                if (isNumber(token) && isOpenBracket(nextToken)) return false;
                else if (isOperator(token) && (isOperator(nextToken) || isCloseBracket(nextToken))) return false;
                else if (isOpenBracket(token)) {
                    numberBrackets++;
                    if (isOperator(nextToken) || isCloseBracket(nextToken) || isOpenBracket(nextToken))
                        return false;
                } else if (isCloseBracket(token)) {
                    numberBrackets--;
                    if (numberBrackets<0) return false;
                    if (isNumber(nextToken) || isOpenBracket(nextToken)) return false;
                }
            } else return false;
        }
        if (isCloseBracket(tokens[tokens.length - 1])) numberBrackets--;
        if (isOpenBracket(tokens[tokens.length - 1])) numberBrackets++;
        if (numberBrackets != 0) return false;

        return isCloseBracket(tokens[tokens.length - 1]) || isNumber(tokens[tokens.length - 1]);
    }


    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    private boolean isOpenBracket(String token) {
        return token.equals("(");
    }

    private boolean isCloseBracket(String token) {
        return token.equals(")");
    }

    private boolean isOperator(String token) {
        return OPERATORS.contains(token) || isComparisonOperator(token) || token.equals("u-");
    }

    private boolean isComparisonOperator(String token) {
        return token.equals("<") || token.equals(">") || token.equals(">=") || token.equals("<=") || token.equals("==")
                || token.equals("!=");
    }

    private byte getPrecedence(String token) {
        if (token.equals("+") || token.equals("-")) {
            return 1;
        } else if (token.equals("/") || token.equals("*") || token.equals("u-"))
            return 3;
        else if (isComparisonOperator(token)) return 2;
        else return 2;
    }

    private void getPartOfTernaryExpression(boolean isRight) {
        Stack<String> newStackRPN = new Stack<String>();
        int numberOfTernOperation1 = 1;
        int numberOfTernOperation2 = 1;
        if (isRight) {
            while (numberOfTernOperation1 != 0) {
                if (!stackRPN.peek().equals("?")) {
                    if (isComparisonOperator(stackRPN.peek())) numberOfTernOperation1++;
                    newStackRPN.push(stackRPN.pop());
                } else {
                    numberOfTernOperation1--;
                    if (numberOfTernOperation1 != 0) newStackRPN.push(stackRPN.pop());
                    else stackRPN.pop();
                }

            }
            while (numberOfTernOperation2 != 0) {
                if (!stackRPN.peek().equals(":")) {
                    if (isComparisonOperator(stackRPN.peek())) numberOfTernOperation2++;
                    stackRPN.pop();
                } else {
                    stackRPN.pop();
                    numberOfTernOperation2--;
                }

            }
        } else {
            while (numberOfTernOperation1 != 0) {
                if (!stackRPN.peek().equals("?")) {
                    if (isComparisonOperator(stackRPN.peek())) numberOfTernOperation1++;
                    stackRPN.pop();
                } else {
                    stackRPN.pop();
                    numberOfTernOperation1--;
                }

            }
            while (numberOfTernOperation2 != 0) {
                if (!stackRPN.peek().equals(":")) {
                    if (isComparisonOperator(stackRPN.peek())) numberOfTernOperation2++;
                    newStackRPN.push(stackRPN.pop());
                } else {
                    numberOfTernOperation2--;
                    if (numberOfTernOperation2 != 0) newStackRPN.push(stackRPN.pop());
                    else stackRPN.pop();
                }

            }
        }
        Collections.reverse(newStackRPN);
        stackRPN.addAll(newStackRPN);
        System.out.println(Arrays.toString(newStackRPN.toArray()));
        System.out.println(Arrays.toString(stackRPN.toArray()));
    }

    private ArrayList<String> findTwoCharacterOperator(String[] tokens) {
        ArrayList<String> newTokens = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            String token = tokens[i];
            String nextToken = tokens[i + 1];
            if (token.equals("=")){
                if (nextToken.equals("=")) {
                    newTokens.add("==");
                    i++;
                }
            } else if (token.equals(">") && nextToken.equals("=")) {
                newTokens.add(">=");
                i++;
            } else if (token.equals("<") && nextToken.equals("=")) {
                newTokens.add("<=");
                i++;
            } else if (token.equals("!") && nextToken.equals("=")) {
                newTokens.add("!=");
                i++;
            } else newTokens.add(token);
        }
        newTokens.add(tokens[tokens.length - 1]);


        return newTokens;
    }

    private ArrayList<String> findUnaryMinus(ArrayList<String> tokens){
        ArrayList<String> newTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size() - 1; i++) {
            String token = tokens.get(i);
            String nextToken = tokens.get(i+1);
            if ((isComparisonOperator(token) || token.equals("(")) && nextToken.equals("-")){
                newTokens.add(token);
                newTokens.add("0");
                newTokens.add("u-");
                i++;
            } else newTokens.add(token);
        }
        newTokens.add(tokens.get(tokens.size()-1));
        System.out.println(Arrays.toString(tokens.toArray()));
        if (newTokens.get(0).equals("-")) {
            newTokens.remove(0);
            newTokens.add(0,"u-");
            newTokens.add(0,"0");
        }

        return newTokens;
    }

    private ArrayList<String> parse (String expression){

        expression = expression.replace(" ", "");
        String[] tokens = expression.split(("((?<=[*/+()><?:=!-])|(?=[*/+()><?:=!-]))"));
        System.out.println(Arrays.toString(tokens));
        System.out.println(Arrays.toString(findTwoCharacterOperator(tokens).toArray()));
        System.out.println(Arrays.toString(findUnaryMinus(findTwoCharacterOperator(tokens)).toArray()));
        return findUnaryMinus(findTwoCharacterOperator(tokens));
    }
}
