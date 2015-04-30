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
