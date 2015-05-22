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
package privacytool.framework.anonymizationrules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import privacytool.framework.data.Data;
import privacytool.framework.data.RDFData;
import privacytool.gui.resultsTab.AnonymizedDatasetPanel;

/**
 * Class to keep anonymization rules
 * @author jimakos
 */
public class AnonymizationRules {

    Map<String, Map<String,String>> anonymizedRules = null;
    
    public AnonymizationRules (){

        this.anonymizedRules = new HashMap<String,Map<String,String>>(); 
    }
    
    
    /**
     * Imports anonymization rules from file
     * @param inputFile
     * @return returns false in case of an error
     * @throws IOException 
     */
    public boolean importRules(String inputFile) throws IOException{
        String del = "->";
        String []temp = null;
        Map<String,String> rules = null; 
        String columnName = null;
        
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        if ((line = br.readLine()) != null){
            if (!line.equals("Anonymization rules:")){
                return false;
            }
        }
        while ((line = br.readLine()) != null) {
           columnName = line;
           rules = new HashMap<>();
           while ((line = br.readLine()) != null) {
               if ( !line.equals("")){
                temp = line.split(del);
                rules.put(temp[0], temp[1]);
               }
               else{
                   break;
               }
           } 
           anonymizedRules.put(columnName, rules);
        }
        
        br.close();
        return true;
    }

    /**
     * Creates rules for a specified column
     * @param column column to create rules for
     * @param initTable initial table
     * @param anonymizedTable anonymized table 
     * @return a map with keys the initial values and value the anonymized values
     */
    public static Map<String, String> createRules(Integer column, TableModel initTable, TableModel anonymizedTable){
            Map<String, String> map = new HashMap<>();
            for(int row=0; row<initTable.getRowCount(); row++){
                String initValue = initTable.getValueAt(row, column).toString();
                String anonymizedValue = anonymizedTable.getValueAt(row, column).toString();
                map.put(initValue, anonymizedValue);
            }
            
            return map;
    }
    
    public Map<String, Map<String, String>> getAnonymizedRules() {
        return anonymizedRules;
    }


    public void setAnonymizedRules(Map<String, Map<String, String>> anonymizeRules) {
        this.anonymizedRules = anonymizeRules;
    }
    
    
    public void setColumnRules(String columnName, Map<String, String> rules){
        this.anonymizedRules.put(columnName, rules);
    }
    
    public void print(){
        for(String s1 : this.anonymizedRules.keySet()){
            System.out.println(s1);
            Map<String, String> map = this.anonymizedRules.get(s1);
            for(String s2 : map.keySet()){
                System.out.println(s2 + " -> " + map.get(s2));
            }
            System.out.println();
        }
    }
    
    public Set<String> getKeyset(){
        return this.anonymizedRules.keySet();
    }
    
    /**
     * Gets the anonymized value for a specific value in a column
     * @param column the column of the value
     * @param key the value for which to return the anonymized value
     * @return the anonymized value of key
     */
    public String get(String column, String key){
        Map<String, String> map = this.anonymizedRules.get(column);
        if(map == null){
            return null;
        }
        
        return map.get(key);
    }
    
    public static void export(String file, Data dataset, JTable initialTable, JTable anonymizedTable, Set<Integer> qids){
        try {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("Anonymization rules:");
                for(Integer qid : qids){
                    
                    //create rules for the column
                    String columnName = dataset.getColumnByPosition(qid);
                    
                    Map<String, String> map = createRules(qid+1, initialTable.getModel(),
                            anonymizedTable.getModel());
                    
                    //write rules to file
                    writer.println(columnName);
                    String del = "->";
                    for (Map.Entry<String, String> entry : map.entrySet()){
//                        writer.print(entry.getKey() + "," + entry.getValue() + "\n");
                        writer.print(entry.getKey()  + del + entry.getValue() + "\n");
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AnonymizedDatasetPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
