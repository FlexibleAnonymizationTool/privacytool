/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privacytool.framework.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import privacytool.framework.dictionary.Dictionary;

/**
 *
 * @author jimakos
 */
public class SetData implements Data {
    private double data[][] = null;   
    private String inputFile = null;
    private int sizeOfRows = 0;
    private int sizeOfCol = 0;
    private String delimeter = null;
    private Map <Integer,String> colNamesType = null;
    private CheckVariables chVar = null;
    private Map <Integer,String> colNamesPosition = null;
    private Map <Integer,Dictionary> dictionary = null;
    
    
    public SetData(String inputFile, String delimeter){
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

        this.delimeter = delimeter;
        this.inputFile = inputFile;
       
        
        try {
            fstream = new FileInputStream(inputFile);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
        
            while ((strLine = br.readLine()) != null)   {
                
                //save column names
                if (FLAG == true){
                    colNamesType.put(0,null);
                    colNamesPosition.put(0,strLine);
                    FLAG = false;
                }
                
                //save column types
                else{
                    temp = strLine.split(delimeter);
                    if (chVar.isInt(temp[0])){
                        colNamesType.put(0, "int");
                    }
                    else if (chVar.isDouble(temp[0])){
                        colNamesType.put(0, "double");
                    }
                    else{
                        colNamesType.put(0, "string");
                        dictionary.put(0, new Dictionary());
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
    

    @Override
    public double[][] getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDataLenght() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        data = new double[sizeOfRows][];
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

    @Override
    public void readDataset() {
        preprocessing();
        save();
    }

    @Override
    public Map<Integer, String> getColumnsTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, String> getColumnsPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Dictionary> getDictionary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary getDictionary(Integer column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDictionary(Integer column, Dictionary dict) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnByName(String column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void replaceColumnDictionary(Integer column, Dictionary dict) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
