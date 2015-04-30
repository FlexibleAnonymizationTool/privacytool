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
package privacytool.framework.algorithms.flash;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import privacytool.framework.algorithms.incognito.GeneralizationNode;
import privacytool.framework.algorithms.incognito.GeneralizationNodeElement;
import privacytool.framework.data.Data;
import privacytool.framework.dictionary.Dictionary;
import privacytool.framework.hierarchy.Hierarchy;
import privacytool.framework.hierarchy.ranges.Range;

/**
 * A frequency set
 * @author serafeim
 */
public class Buffer {
    Data data = null;
    Map<Integer, Hierarchy> hierarchies = null;
    
    Map<GeneralizedRow, Integer> frequencies = new HashMap<>();
    
    public Buffer(Data _data,  Map<Integer, Hierarchy> _hierarchies){
        hierarchies = _hierarchies;
        data = _data;
    }
    
    /**
     * computes the frequency set for the specified root graph node
     * @param node the node 
     * @param qidColumns 
     */
    public void compute(LatticeNode node, int[] qidColumns){
        double[][] dataset = data.getData();
        
        for(int i=0; i<dataset.length; i++){       
            GeneralizedRow generalizedRow = project(node, qidColumns, dataset[i]);
            
            Integer count;
            if((count = frequencies.get(generalizedRow)) != null){
                frequencies.put(generalizedRow, ++count);
            }
            else {
                frequencies.put(generalizedRow, 1);
            }
        }
    } 
    
    
    /**
     * projects QI columns of row according to node's elements
     * @param node a generalization graph node
     * @param row the row of the dataset to be generalized
     * @return the generalized QI columns of the row
     */
    private GeneralizedRow project(LatticeNode node, int[] qidColumns, double[] row){

        GeneralizedRow gRow = new GeneralizedRow(node.getTransformation().length);

        int j = 0;
        for(int k=0; k<node.getTransformation().length; k++){
            Hierarchy h = hierarchies.get(qidColumns[k]);
            
            //get the value of the specified attribute
            Object rowValue = null; 
            if(data.getColumnsTypes().get(qidColumns[k]).equals("string")){
                Dictionary dict = data.getDictionary(qidColumns[k]);
                rowValue = dict.getIdToString((int)row[qidColumns[k]]);
            } 
            else {
                rowValue = row[qidColumns[k]];
            }

            //generalize value
            for(int i=0; i<node.getTransformation()[k]; i++){
                
                if(h.getHierarchyType().equals("range") && i==0){
                    
                    rowValue = h.getParent((Double)rowValue);
//                    System.out.println("range found " + rowValue);
                }
                else{
                    rowValue = h.getParent(rowValue);
                }
            }

            gRow.generalizedColumns[j] = rowValue.toString();
            j++;
        }
        
        return gRow;
    }
    
    /**
     * Determines if the frequency set is k-Anonymous with respect to k
     * @param k the parameter k of k-Anonymity
     * @return true if the frequency set is k-Anonymous, false otherwise
     */
    public boolean isKAnonymous(int k){
        boolean isAnonymous = true;
       
        for(GeneralizedRow distinctRow : frequencies.keySet()){
            Integer count = frequencies.get(distinctRow);
            if(count < k){
                isAnonymous = false;
                break;
            }
        }
        return isAnonymous;
    }
    
    /**
     * Computes frequency set from the parent's frequency set (for non-root nodes)
     * @param node a generalization graph node
     * @param parentNode
     * @param parentNodeBuffer
     * @param qidColumns
     */
    public void compute(LatticeNode node, LatticeNode parentNode, Buffer parentNodeBuffer, int[] qidColumns) {

        int[] nodeTransf = node.getTransformation();
        int[] parentNodeTransf = parentNode.getTransformation();
        
        for(GeneralizedRow pRow : parentNodeBuffer.getFrequencies().keySet()){  
            GeneralizedRow gRow = new GeneralizedRow(nodeTransf.length);
            
            for(int i=0; i<pRow.generalizedColumns.length; i++){
                
                if(nodeTransf[i] == parentNodeTransf[i]){
                    gRow.generalizedColumns[i] = pRow.generalizedColumns[i];
                }
                //if this is the element to be further generalized
                else{
                    int k = nodeTransf[i] - parentNodeTransf[i];
                    Hierarchy h = hierarchies.get(qidColumns[i]);
                    Object value = pRow.generalizedColumns[i];
                    Object parent = null;
                    
                    if(h.getHierarchyType().equals("range")){
                        if(parentNodeTransf[i] == 0 && nodeTransf[i] > 0){
                            Double doubleValue = Double.parseDouble(value.toString());
                            parent = h.getParent(doubleValue);
                            
                            for(int j=0; j<k-1; j++)
                                parent = h.getParent(parent);
                        }
                        else{
                            Range rangeValue = Range.parseRange(value.toString());
                            parent = rangeValue;
                            for(int j=0; j<k; j++)
                                parent = h.getParent(parent);
                        }
                    }
                    else{
                        if(data.getColumnsTypes().get(qidColumns[i]).equals("string")){
                            parent = value;
                            for(int j=0; j<k; j++)
                                parent =  h.getParent(parent);
                        }
                        else{
                            Double doubleValue = Double.parseDouble(value.toString());
                            parent = doubleValue;
                            for(int j=0; j<k; j++)
                                parent = h.getParent(parent);  
                        }  
                    }
                    gRow.generalizedColumns[i] = parent.toString();
                }  
            }
            
            //compute frequencies based on parent's respective frequencies
            Integer count;
            Integer curCount = parentNodeBuffer.getFrequencies().get(pRow);   
            if((count = frequencies.get(gRow)) != null){
                frequencies.put(gRow, count + curCount);
            }
            else{
                frequencies.put(gRow, curCount);
            }
        }
    }
    
    public int getSize(){
        return this.frequencies.size();
    }
    
    /**
     * Getter of frequencies map
     * @return frequencies map
     */
    public Map<GeneralizedRow, Integer> getFrequencies() {
        return frequencies;
    }
    
    private class GeneralizedRow{
        public String[] generalizedColumns = null;

 
        public GeneralizedRow (int size){
            generalizedColumns = new String[size];
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Arrays.deepHashCode(this.generalizedColumns);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GeneralizedRow other = (GeneralizedRow) obj;
            if (!Arrays.deepEquals(this.generalizedColumns, other.generalizedColumns)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return  Arrays.toString(generalizedColumns);
        }
    } 
}
