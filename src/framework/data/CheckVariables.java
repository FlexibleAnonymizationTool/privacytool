/* 
 * Copyright (C) 2015 "IMIS-Athena R.C.",
 * Institute for the Management of Information Systems, part of the "Athena" 
 * Research and Innovation Centre in Information, Communication and Knowledge Technologies.
 * [http://www.imis.athena-innovation.gr/]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
