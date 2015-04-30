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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import privacytool.framework.dictionary.Dictionary;

/**
 * A class managing text data
 * @author serafeim
 */
public class TXTData implements Data{
    private double data[][] = null;   
    private String inputFile = null;
    private int sizeOfRows = 0;
    private int sizeOfCol = 0;
    private String delimeter = null;
    private Map <Integer,String> colNamesType = null;
    private CheckVariables chVar = null;
    private Map <Integer,String> colNamesPosition = null;
    private Map <Integer,Dictionary> dictionary = null;
    
    
    public TXTData(String inputFile, String del){
        this.inputFile = inputFile;
        colNamesType = new TreeMap<Integer,String>();
        colNamesPosition = new HashMap<Integer,String>();
        chVar = new CheckVariables();
        dictionary = new HashMap <Integer,Dictionary>();
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String []temp = null;
        String []colNames = null;
        boolean FLAG = true;

        //assing delimeter
        if (del != null){
            this.delimeter = del;
        }
        else{
            this.delimeter = " ";
        }
        
        try {
            fstream = new FileInputStream(inputFile);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
        
            while ((strLine = br.readLine()) != null)   {
                
                //save column names
                if (FLAG == true){
                    colNames = strLine.split(delimeter);
                    for ( int i = 0 ; i < colNames.length ; i ++){
                        colNamesType.put(i,null);
                        colNamesPosition.put(i,colNames[i]);
                    }
                    FLAG = false;
                }
                
                //save column types
                else{
                    temp = strLine.split(delimeter);
                    for ( int i = 0 ; i < temp.length ; i ++ ){
                        if (chVar.isInt(temp[i])){
                            colNamesType.put(i, "int");
                        }
                        else if (chVar.isDouble(temp[i])){
                            colNamesType.put(i, "double");
                        }
                        else{
                            colNamesType.put(i, "string");
                            dictionary.put(i, new Dictionary());
                        }
                    }
                    sizeOfCol = temp.length;
                    break;
                }
                
            }
            
            in.close();
            
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Gets the array of the loaded dataset
     * @return 2-dimensional array of the loaded dataset
     */
    @Override
    public double[][] getData() {
        
        return data;
    }

    
    @Override
    public void setData() {
        
    }

    /**
     * Gets the length of the dataset array
     * @return length of the dataset
     */
    @Override
    public int getDataLenght() {
        return data.length;
    }
    
    /**
     * Prints the dataset
     */
    @Override
    public void print(){
        int i,j;
        for (i = 0 ; i < data.length ; i ++){
            for (j = 0 ; j < data[i].length ; j++){
                System.out.print(data[i][j]+",");
            }
            System.out.println();
        }
    }

    /**
     * Executes a preprocessing of the dataset
     */
    @Override
    public void preprocessing() {
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String []temp = null;
        int i = 0;
        int counter = -1;
        
        try {
            fstream = new FileInputStream(inputFile);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
        
            //counts lines of the dataset
            while ((strLine = br.readLine()) != null)   {   
                counter++;
            }
            
            System.out.println("counter = " + counter);
            sizeOfRows = counter;
            in.close();
            
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Loads dataset from file to memory
     */
    @Override
    public void save() {
        data = new double[sizeOfRows][sizeOfCol];
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String []temp = null;
        String []colNames = null;
        int counter = 0;
        int stringCount = 0;
        boolean FLAG = true;
        
        try {
            fstream = new FileInputStream(inputFile);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
        
            while ((strLine = br.readLine()) != null){
                
                //do not read the fist line
                if (FLAG == true){
                    colNames = strLine.split(delimeter);
                    FLAG = false;
                }
                else{

                    temp = strLine.split(delimeter);
                    for (int i = 0; i < temp.length ; i ++ ){
                        if ( colNamesType.get(i).contains("int") ){
                            data[counter][i] = Integer.parseInt(temp[i]);
                        }
                        else if (colNamesType.get(i).contains("double")){
                            data[counter][i] = Double.parseDouble(temp[i]);
                        }
                        else{
                            Dictionary tempDict = dictionary.get(i);
                            
                            //if string is not present in the dictionary
                            if (tempDict.containsStringToId(temp[i]) == false){
                                tempDict.putIdToString(stringCount, temp[i]);
                                tempDict.putStringToId(temp[i],stringCount);
                                dictionary.put(i, tempDict);
                                data[counter][i] = stringCount;
                                stringCount++;
                            }
                            else{
                                //if string is present in the dictionary, get its id
                                int stringId = tempDict.getStringToId(temp[i]);
                                data[counter][i] = stringId;
                            }
                        }
                    }
                    counter++;
                }
            }
        
            in.close();
            
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }


    /**
     * Reads dataset from file (preprocessing and load)
     */
    @Override
    public void readDataset() {
        preprocessing();
        save();
    }

    /**
     * Gets dictionary for the specified column
     * @param column the number of the column
     * @return the dictionary for the column
     */
    @Override
    public Dictionary getDictionary(Integer column){
        return this.dictionary.get(column);
    }
    
    /**
     * Gets column types
     * @return a map with the column types
     */
    @Override
    public Map<Integer, String> getColumnsTypes() {
        return colNamesType;
    }

    /**
     * Gets column names
     * @return a map with the column names by position
     */
    @Override
    public Map<Integer, String> getColumnsPosition() {
        return colNamesPosition;
    }

    /**
     * Gets all dictionaries
     * @return a map with with the column dictionaries by position
     */
    @Override
    public Map<Integer, Dictionary> getDictionary() {
        return dictionary;
    }
    
    /**
     * Finds the column number of the column name specified
     * @param column the column name to search for
     * @return the column number of the given column
     */
    @Override
    public int getColumnByName(String column){
        for(Integer i : this.colNamesPosition.keySet()){
            if(this.colNamesPosition.get(i).equals(column)){
                return i;        
            }
        }
        return -1;
    }

    /**
     * Sets a new dictionary for a specific column
     * @param column the column number
     * @param dict the new dictionary
     */
    @Override
    public void setDictionary(Integer column, Dictionary dict) {
        this.dictionary.put(column, dict);
    }

    /**
     * Replaces the dictionary of a column with a new one. Updates values in this
     * column with those taken from the new dictionary
     * @param column the column number
     * @param dict the new dictionary
     */
    @Override
    public void replaceColumnDictionary(Integer column, Dictionary dict) {
        Dictionary curDict = this.dictionary.get(column);
        
        for (double[] row : data) {
            
            //retrieve actual value from dictionary
            String columnValue = curDict.getIdToString((int)row[column]);
            
            //replace with value from new dictionary
            row[column] = dict.getStringToId(columnValue);
            
        }
        
        //set given dictionary as the new one
        setDictionary(column, dict);
    }
}
