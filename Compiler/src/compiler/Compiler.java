/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, Exception {
        Scanner input = new Scanner(System.in);
        Scan sc = new Scan(); 
        String filename = input.nextLine();
        sc.Scanning(filename);
        
    }

   
    
}
