/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.data;

/**
 * Class for checking type of variables
 * @author jimakos
 */
public class CheckVariables {
    
    public CheckVariables(){
    }
    
    /**
     * Checks if given string is an integer
     * @param s the string to be checked
     * @return true if it can represent an integer, false otherwise
     */
    public boolean isInt(String s){
        try{ 
            int i = Integer.parseInt(s); 
            return true; 
        }
        catch(NumberFormatException er){ 
            return false; 
        }
    }
    
    
    /**
     * Checks if given string is a double
     * @param s the string to be checked
     * @return true if it can represent a double, false otherwise
     */
    public boolean isDouble(String s){
        try{ 
            double d = Double.parseDouble(s); 
            return true; 
        }
        catch(NumberFormatException er){ 
            return false; 
        }
    }
       
}
