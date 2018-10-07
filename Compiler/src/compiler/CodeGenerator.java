/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author user
 */
public class CodeGenerator {

    private static File file = new File("prog1.txt");
    private static BufferedWriter out;
    private static ArrayList<String> exp = new ArrayList<>();
    private static ArrayList<String> resw = new ArrayList<>();
    private static int counter =0;

    public void write(ArrayList<String> typeOfTokens, String[] tokens, String lineType) throws IOException {
        out = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\" + "GeneratedCode.txt",true));

        if (lineType.equalsIgnoreCase("HEADER")) {
            out.write(tokens[1] + "  START " + " 0");
            out.newLine();
            out.write("EXTREF\tXREAD,XWRITE");
            out.newLine();

        } else if (lineType.equalsIgnoreCase("VAR")) {
            for (int i = 0; i < tokens.length; i++) {
                if (typeOfTokens.get(i).equalsIgnoreCase("ID")) {
                    resw.add(tokens[i]);
                }
            }
        } else if (lineType.equalsIgnoreCase("end")) {
            outVars();
            out.write("END");
            out.newLine();

        } else if (lineType.equalsIgnoreCase("assign")) {
            
            exp = infixToPostfix(fixTokens(tokens));
            System.out.println(exp);
            evaluate(exp);
            out.write("\tSTA "+tokens[0]);
            out.newLine();
            
        } else if (lineType.equalsIgnoreCase("stmt")) {

            if (tokens[0].equalsIgnoreCase("read")) {
                out.write("\t+JSUB XREAD");
                out.newLine();
                out.write("\tWORD  " + countParam(typeOfTokens));
                out.newLine();
                for (int i = 2; i < typeOfTokens.size(); i++) {
                    if (typeOfTokens.get(i).equalsIgnoreCase("ID")) {
                        out.write("\tWORD  " + tokens[i]);

                        out.newLine();
                    }
                }

            } else if (tokens[0].equalsIgnoreCase("write")) {

                out.write("\t+JSUB XWRITE");
                out.newLine();
                out.write("\tWORD  " + countParam(typeOfTokens));
                out.newLine();

                for (int i = 2; i < typeOfTokens.size(); i++) {
                    if (typeOfTokens.get(i).equalsIgnoreCase("ID")) {
                        out.write("\tWORD  " + tokens[i]);

                        out.newLine();
                    }
                }
            }

        }
        out.close();
    }

    private static void outVars() throws IOException
    {
        for(int i=0;i<resw.size();i++)
        {
            out.write(resw.get(i)+"\tRESW\t1");
            out.newLine();
        }
    }
    
    private int countParam(ArrayList<String> typeOfTokens) {
        int count = 0;
        for (int i = 2; i < typeOfTokens.size(); i++) {
            if (typeOfTokens.get(i).equalsIgnoreCase("ID")) {
                count++;
            }
        }
        return count;
    }

    private String[] fixTokens(String[] tokens)
    {
        String[] fixed = new String[tokens.length-3];
        
        for(int i=2;i<tokens.length-1;i++)
        {
            fixed[i-2] = tokens[i];
        }
        return fixed;
    }
    
     private static int Prec(String ch)
    {
        switch (ch)
        {
        case "+":
        case "-":
            return 1;
      
        case "*":
       
            return 2;
        
        }
        return -1;
    }
      
    // The main method that converts given infix expression
    // to postfix expression. 
    private static ArrayList<String> infixToPostfix(String[] exp)
    {
        // initializing empty String for result
        String result = new String("");
         
        // initializing empty stack
        Stack<String> stack = new Stack<>();
        ArrayList<String> postfix = new ArrayList<>();
        
        for (int i = 0; i<exp.length; ++i)
        {
            String c = exp[i];
             
             // If the scanned character is an operand, add it to output.
            if (!(c.equalsIgnoreCase("+")) && !(c.equalsIgnoreCase("-")) && !(c.equalsIgnoreCase("*")) && !(c.equalsIgnoreCase("(")) && !(c.equalsIgnoreCase(")"))  ){
                
               
               postfix.add(c);
            }
            // If the scanned character is an '(', push it to the stack.
            else if (c.equalsIgnoreCase("(") )
            {  stack.push(c);
                
            //  If the scanned character is an ')', pop and output from the stack 
            // until an '(' is encountered.
            } else if (c.equalsIgnoreCase(")") )
            {
                while (!stack.isEmpty() && !(stack.peek().equalsIgnoreCase("(")))
                    
                    postfix.add(stack.pop());
                if (!stack.isEmpty() && !(stack.peek().equalsIgnoreCase("(")))
                    return null; // invalid expression                
                else
                    stack.pop();
            }
            else // an operator is encountered
            {
                while (!stack.isEmpty() && Prec(c) <= Prec(stack.peek()))
                   
                    postfix.add(stack.pop());
                stack.push(c);
            }
      
        }
      
        // pop all the operators from the stack
        while (!stack.isEmpty())
            postfix.add(stack.pop());
            
        return postfix;
    }
    
    
    
          private static int CountOperators(ArrayList<String> exp)
    {
        int count =0;
        for(int i=0;i<exp.size();i++)
        {
            if(exp.get(i).equals("+") || exp.get(i).equals("*"))
            {
                count++;
            }
           
        }
        return count;
    }
     
     private static int findexp(ArrayList<String> exp)
    {
        for(int i=0;i<exp.size();i++)
        {
            if(exp.get(i).equals("+") || exp.get(i).equals("*"))
            {
                
                return i;
            }
        }
        return -1;
    }
   
     
    private static void evaluate(ArrayList<String> exp) throws IOException
    {
        int loc = 0;
        while(CountOperators(exp)!=0)
        {
            loc = findexp(exp);
            
            
            
            if(exp.get(loc).equals("+"))
            {
                Add(loc,exp);
                
            }
            else if(exp.get(loc).equals("*"))
            {
                MUL(loc,exp);
            }  
            
            System.out.println(exp);
            
        }
        
    }
    
    private static void MUL(int loc , ArrayList<String> exp) throws IOException
    {
        if(!(exp.get(loc-1).matches("T\\d?")) && !(exp.get(loc-2).matches("T\\d?")))
        {
            
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tMUL "+exp.get(loc-1));
            out.newLine();
            
            
        }
        else if(exp.get(loc-1).matches("T\\d?") && !(exp.get(loc-2).matches("T\\d?")))
        {
            out.write("\tLDA "+exp.get(loc-1));
            out.newLine();
            out.write("\tMUL "+exp.get(loc-2));
            out.newLine();
        }   
        else if(!(exp.get(loc-1).matches("T\\d?")) && exp.get(loc-2).matches("T\\d?"))
        {
            
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tMUL "+exp.get(loc-1));
            out.newLine();
        }
        else if(exp.get(loc-1).matches("T\\d?") && exp.get(loc-2).matches("T\\d?"))
        {
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tMUL "+exp.get(loc-1));
            out.newLine();
            
        }
            exp.remove(loc);
            exp.remove(loc-1);
            exp.set(loc-2,"T"+counter);
            resw.add("T"+counter);
            if(CountOperators(exp)!=0){
            out.write("\tSTA "+"T"+counter);
            out.newLine();
            }
            counter++;  
    }
    
    private static void Add(int loc , ArrayList<String> exp) throws IOException
    {
        if(!(exp.get(loc-1).matches("T\\d?")) && !(exp.get(loc-2).matches("T\\d?")))
        {
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tADD "+exp.get(loc-1));
            out.newLine();
        }
        else if(exp.get(loc-1).matches("T\\d?") && !(exp.get(loc-2).matches("T\\d?")))
        {
            out.write("\tLDA "+exp.get(loc-1));
            out.newLine();
            out.write("\tADD "+exp.get(loc-2));
           out.newLine();
        }
        else if(!(exp.get(loc-1).matches("T\\d?")) && exp.get(loc-2).matches("T\\d?"))
        {
            
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tADD "+exp.get(loc-1));
            out.newLine();
        }
        else if(exp.get(loc-1).matches("T\\d?") && exp.get(loc-2).matches("T\\d?"))
        {
            out.write("\tLDA "+exp.get(loc-2));
            out.newLine();
            out.write("\tADD "+exp.get(loc-1));
            out.newLine();
        }
            exp.remove(loc);
            exp.remove(loc-1);
            exp.set(loc-2,"T"+counter);
            resw.add("T"+counter);
            if(CountOperators(exp)!=0){
            out.write("\tSTA "+"T"+counter);
            out.newLine();
            }
            counter++;  
            
    }
}
