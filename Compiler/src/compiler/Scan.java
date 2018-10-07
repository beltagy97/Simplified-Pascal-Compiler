/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author user
 */
public class Scan {

    private static String line = "";
    private String id = "";
    private static String[] token;
    private static ArrayList<String> SYMTAB = new ArrayList<>();
    private static ArrayList<String> typeOfTokens = new ArrayList<>();
    private static int counter = 0;
    private static String del = ",|\\(|\\)|\\+|\\*|\\=|\\;";
    private Matcher matcher;
    private static int lineCounter = 1;
    private String lineType = "";
    Parser parse = new Parser();

    public void Scanning(String fileName) throws FileNotFoundException, Exception {
        FileInputStream ioFile = new FileInputStream(System.getProperty("user.dir") + "\\" + fileName);
        Scanner input = new Scanner(ioFile);

        line = input.nextLine();
        
        Pattern start = Pattern.compile("\\s*\\bPROGRAM\\b\\s*\\w+\\s*");
        matcher = start.matcher(line);
        System.out.println(line);
       
        if (matcher.matches()) {
            
            token = line.split("\\s");
           
            identifyTokens();
            lineType = "HEADER";
            parse.parse(typeOfTokens, token, lineType, lineCounter);
            typeOfTokens.clear();

            while (input.hasNext()) {
                counter = 0;

                line = input.nextLine();
                System.out.println(line);
                lineCounter++;
                
                if(line.equals(""))
                {
                    continue;
                }
                
                //handles VAR .. ,..,...
                if (line.matches("\\s*\\bVAR\\b\\s*")) {
                    line = input.nextLine();
                    lineCounter++;

                    Tokenizer();
                   token =  trimSpaces(token);
                    intializeSymTab();
                    lineType = "VAR";
                   // printTokens();
                    //checks brackets of READ and throws error if missing bracket 
                } else if (line.matches("\\s*\\bBEGIN\\b\\s*")) {
                    Tokenizer();
                    lineType = "begin";

                } else if (line.contains("READ")) {
                    if (line.matches("\\s*\\bREAD\\b\\s*\\([\\w,]+\\);\\s*")) {
                        Tokenizer();
                       token = trimSpaces(token);
                        checkVariables(lineCounter);
                        lineType = "stmt";

                    } else {
                        throw new Exception("SYNTAX ERROR in line " + lineCounter);
                    }
                } else if (line.contains("WRITE")) {
                    if (line.matches("\\s*\\bWRITE\\b\\s*\\([\\w,]+\\)\\s*")) {
                        Tokenizer();
                        token = trimSpaces(token);
                        //printTokens();
                        checkVariables(lineCounter);
                        lineType = "stmt";
                        
                    } else {
                        
                        throw new Exception("SYNTAX ERROR in line " + lineCounter);
                    }
                } //checks that last line must be END or END.
                else if (line.matches("\\s*END\\.?\\s*")) {
                    Tokenizer();
                    lineType = "end";
                } //else it is an expression
                else {
                    Tokenizer();
                    token = trimSpaces(token);
                    fixTokens();
                    
                    if (token[1].equalsIgnoreCase(":=")) {
                        
                        lineType = "assign";
                        
                    } else {
                        lineType = "exp";
                    }
                }
               
                token = trimSpaces(token);
                //printTokens();
                identifyTokens();
                parse.parse(typeOfTokens, token, lineType,lineCounter);
                // printMap(typeOfTokens);
                typeOfTokens.clear();
                
            }
        }

    }

    private static void Tokenizer() {
        StringTokenizer st = new StringTokenizer(line, del, true);

        token = new String[st.countTokens()];
        while (st.hasMoreTokens()) {

            token[counter] = (st.nextToken().trim()).replace(" ", "");
            counter++;
           
        }

    }

    private void printTokens() {
        for (String x : token) {
            System.out.println(x);
        }
        System.out.println("");
    }

    private static void intializeSymTab() {
        for (int i = 0; i < token.length; i++) {

            if (token[i].equals(",")) {
                continue;
            }
            SYMTAB.add(token[i]);
        }

        //printMap(SYMTAB);
    }

    private boolean checkVariables(int lineNumber) throws Exception {
        int y=2;
        if(line.contains("WRITE"))
        {y=1;
        }
            
        for (int i = 2; i < token.length - y; i++) {
            System.out.println(token[i]);
            if (token[i].contains(",")) {
                continue;
            }
            if (SYMTAB.contains(token[i])) {
                continue;
            } else {
                throw new Exception("SYNTAX ERROR in line " + lineNumber);

            }
        }
        return true;
    }

    private void fixTokens() {
        String assign = ":=";
        if (token[0].charAt(token[0].length() - 1) == ':') {
            token[0] = token[0].substring(0, token[0].length() - 1);
            token[1] = assign;
            token[0] = token[0].trim();
            token[1] = token[1].trim();
        }
    }

    private static void identifyTokens() {
        String KEYWORD = "\\bREAD\\b|\\bWRITE\\b|\\bBEGIN\\b";
        String LITERAL = "\\d+";
        String ASSIGNMENTOPERATOR = "(:=)";
        String OPERATOR = "\\+|\\-|\\*|\\/";
        String OPENINGBRACKET = "\\(";
        String CLOSINGBRACKET = "\\)";
        String SEMICOLON = "\\;";
        String ID = "\\w+";
        String COMMA = "\\,";
        String END = "END\\.?";
        String PROGRAM = "\\bPROGRAM\\b";

        for (int i = 0; i < token.length; i++) {

            if (token[i].matches(KEYWORD)) {

                typeOfTokens.add("KEYWORD");
            } else if (token[i].matches(PROGRAM)) {
                typeOfTokens.add("PROG");
            } else if (token[i].matches(LITERAL)) {
                typeOfTokens.add("LITERAL");
            } else if (token[i].matches(ASSIGNMENTOPERATOR)) {
                typeOfTokens.add("ASSIGNMENTOPERATOR");
            } else if (token[i].matches(OPERATOR)) {
                typeOfTokens.add("OPERATOR");
            } else if (token[i].matches(OPENINGBRACKET)) {
                typeOfTokens.add("OPENINGBRACKET");
            } else if (token[i].matches(CLOSINGBRACKET)) {
                typeOfTokens.add("CLOSINGBRACKET");
            } else if (token[i].matches(ID)) {
                typeOfTokens.add("ID");
            } else if (token[i].matches(SEMICOLON)) {
                typeOfTokens.add("SEMICOLON");
            } else if (token[i].matches(COMMA)) {
                typeOfTokens.add("COMMA");
            } else if (token[i].matches(END)) {
                typeOfTokens.add("KEYWORD");
            } else {
                typeOfTokens.add("NOP");

            }
        }
    }
    
     public static String[] trimSpaces(String[] assembly) {
        List<String> list = new ArrayList<String>();
        for (String text : assembly) {
            if (text != null && text.length() > 0) {
                list.add(text);
            }
        }
        assembly = list.toArray(new String[0]);
        return assembly;
    }

    private static void printMap(ArrayList<String> identifyToken) {
        for (int i = 0; i < identifyToken.size(); i++) {
            System.out.print(identifyToken.get(i) + "  " + token[i] + "   ");
        }
        System.out.println("");
    }

}
