/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privacytool.framework.data;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JTable;
import privacytool.framework.dictionary.Dictionary;

/**
 *
 * @author jimakos
 */
public class SETData implements Data {
    private double data[][] = null;   
    private String inputFile = null;
    private int sizeOfRows = 0;
    private int sizeOfCol = 0;
    private String delimeter = null;
    private Map <Integer,String> colNamesType = null;
    private CheckVariables chVar = null;
    private Map <Integer,String> colNamesPosition = null;
    private Map <Integer,Dictionary> dictionary = null;
    
    
    public SETData(String inputFile, String delimeter){
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
        return data; 
    }

    @Override
    public void setData(double[][] _data) {
        this.data = _data;
    }

    @Override
    public int getDataLenght() {
        return data.length;
    }

    @Override
    public void print() {
        int i,j;
        for (i = 0 ; i < data.length ; i ++){
            System.out.println("here : " + data.length + "\t" +data[i].length );
            for (j = 0 ; j < data[i].length ; j++){
                System.out.print(data[i][j]+",");
            }
            System.out.println();
        }
    }

    @Override
    public void save() {
        data = new double[sizeOfRows][];
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String []temp = null;
        String colNames = null;
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
                    colNames = strLine;
                    FLAG = false;
                }
                else{

                    temp = strLine.split(delimeter);
                    data[counter] = new double[temp.length];
                    for (int i = 0; i < temp.length ; i ++ ){
                       Dictionary tempDict = dictionary.get(0);
                            
                            //if string is not present in the dictionary
                            if (tempDict.containsString(temp[i]) == false){
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
                        //}
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
        return colNamesType;
    }

    @Override
    public Map<Integer, String> getColumnsPosition() {
        return colNamesPosition;
    }

    @Override
    public Map<Integer, Dictionary> getDictionary() {
        return this.dictionary;
    }

    @Override
    public Dictionary getDictionary(Integer column) {
        return this.dictionary.get(column);
    }

    @Override
    public void setDictionary(Integer column, Dictionary dict) {
        this.dictionary.put(column, dict);
    }

    @Override
    public int getColumnByName(String column) {
        for(Integer i : this.colNamesPosition.keySet()){
            if(this.colNamesPosition.get(i).equals(column)){
                return i;        
            }
        }
        return -1;
    }

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

    @Override
    public void export(String file, JTable initialTable, JTable anonymizedTable, Set<Integer> qids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getColumnByPosition(Integer columnIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
