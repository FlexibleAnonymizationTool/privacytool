/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privacytool.framework.anonymizationrules;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.TableModel;

/**
 *
 * @author jimakos
 */
public class AnonymizationRules {

    Map<String, Map<String,String>> anonymizedRules = null;
    
    public AnonymizationRules (){

        this.anonymizedRules = new HashMap<String,Map<String,String>>(); 
    }
    
    
    public boolean importRules(String inputFile) throws IOException{
        String del = ",";
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
//            System.out.println("line1 = " + line);
           columnName = line;
           rules = new HashMap<String,String>();
           while ((line = br.readLine()) != null) {
//               System.out.println("line2 = " + line);
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

    public Map<String, String> createRules(Integer column, TableModel initTable, TableModel anonymizedTable){
            

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
    
    
    
    
}
