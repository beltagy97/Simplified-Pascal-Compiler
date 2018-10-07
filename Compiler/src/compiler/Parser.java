/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Parser {

    private ArrayList<String> typeOfTokens;
    private String[] tokens;
    private String lineType;
    private int lineNumber;
    private CodeGenerator generate = new CodeGenerator();

    public void parse(ArrayList<String> typeOfTokens, String[] tokens, String lineType, int lineNumber) throws Exception {
        this.typeOfTokens = typeOfTokens;
        this.tokens = tokens;
        this.lineType = lineType;
        this.lineNumber = lineNumber;

//        System.out.print(lineType + "  ");
//        for (int i = 0; i < tokens.length; i++) {
//            System.out.print(tokens[i] + " " + typeOfTokens.get(i) + " ");
//        }
//        System.out.println("");
        if (checkGrammer()) {
            generate.write(typeOfTokens, tokens, lineType);
        } else {
            throw new Exception("SYNTAX ERROR in line " + lineNumber);
        }

    }

    private boolean checkGrammer() throws Exception {
        switch (lineType) {
            case "stmt": {
                if (tokens[0].equalsIgnoreCase("read")) {

                    return checkIdList(2, typeOfTokens.size() - 2);
                } else if (tokens[0].equalsIgnoreCase("write")) {
                    return checkIdList(2, typeOfTokens.size() - 2);
                }
                break;
            }
            case "VAR": {
                return checkIdList(0, typeOfTokens.size());
            }
            case "assign": {
                return checkAssign();
            }
        }
        return true;
    }

    private boolean checkIdList(int start, int end) {
        boolean flag = true;

        for (int i = start; i < end; i++) {

            if (typeOfTokens.get(i).equalsIgnoreCase("ID") || typeOfTokens.get(i).equalsIgnoreCase("COMMA")) {
            } else {
                flag = false;
            }
        }
        return flag;
    }

    private boolean checkAssign() throws Exception {
        if (typeOfTokens.get(0).equalsIgnoreCase("ID") && typeOfTokens.get(1).equalsIgnoreCase("ASSIGNMENTOPERATOR")) {

            return checkExpression(2, tokens.length);
        } else {
            return false;
        }
    }

    private boolean checkExpression(int start, int end) throws Exception {
        int countID = 0;
        int countOperator = 0;

        for (int i = start; i < end - 1; i++) {
            
            if (typeOfTokens.get(i).equalsIgnoreCase("ID")) {
                countID++;
            } else if (typeOfTokens.get(i).equalsIgnoreCase("OPERATOR")) {
                countOperator++;
            } else if (typeOfTokens.get(i).equalsIgnoreCase("OPENINGBRACKET") || typeOfTokens.get(i).equalsIgnoreCase("CLOSINGBRACKET")) {
              continue;
            } else {
                return false;
            }
        }
        if (typeOfTokens.get(end - 1).equalsIgnoreCase("SEMICOLON")) {

            if (countOperator == (countID - 1)) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new Exception("SYNTAX ERROR in line " + lineNumber);
        }
    }
}
