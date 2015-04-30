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
package privacytool.framework.algorithms.incognito;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A node of the GeneralizationGraph with multiple node elements
 * @author serafeim
 */
public class GeneralizationNode {

    GeneralizationNodeElement[] nodeElements = null;
    boolean marked = false;
    boolean processed = false;
    FrequencySet fset = null;
    int accessesRemaining = -1;
    
    /**
     * Constructor of the class
     * @param l the list of GeneralizationNodeElements to be assigned to 
     * the specific node
     */
    public GeneralizationNode(GeneralizationNodeElement[] l){
        nodeElements = l;
    }
    
    @Override
    public String toString() {
        String nodeName = "";
        for(GeneralizationNodeElement e : nodeElements){
            nodeName = nodeName + " " + e.toString();
        }
        return "["+nodeName.trim()+"]";
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        GeneralizationNodeElement[] objElements = ((GeneralizationNode) obj).getNodeElements();
        
        boolean isEquals = true;

        for(int i=0; i<nodeElements.length; i++){
            if(!nodeElements[i].equals(objElements[i])){
                isEquals = false;
                break;
            }
        }
        
        return isEquals;
    }

    @Override
    public int hashCode() {  
        return this.toString().hashCode();
    }
    
    /**
     * Getter of the list of node elements that this node contains
     * @return the list of node elements that this node contains
     */
    public GeneralizationNodeElement[] getNodeElements(){
        return this.nodeElements;
    }
    
    
    /**
     * copy constructor
     * @param old the instance of class we want to make a copy of
     */
    public GeneralizationNode(GeneralizationNode old) {
        int length = old.getNodeElements().length;
        this.nodeElements = new GeneralizationNodeElement[length];

        for(int i=0;  i < old.nodeElements.length; i++){
            GeneralizationNodeElement e = old.nodeElements[i];
            this.nodeElements[i] = new GeneralizationNodeElement(e.qid, e.level);
        }
    }
    
    /**
     * Gets the height of a generalization node (the sum of node elements level)
     * @return the height of the node
     */
    public int getNodeHeight(){
        int level = 0;
        
        for(GeneralizationNodeElement e : nodeElements){
            level += e.level;
        }
        
        return level;
    }

    /**
     * Gets if the node is marked
     * @return true if is marked, false otherwise
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Marks the node
     */
    public void mark() {
        this.marked = true;
    }
            
    /**
     * Gets the node elements of the node as a set 
     * @return a set with the node elements
     */
    public Set<GeneralizationNodeElement> getNodeElementsSet(){
        return (new LinkedHashSet<>(Arrays.asList(nodeElements)));
    }

    /**
     * Gets if the node is already processed
     * @return true if node is processed, false otherwise
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Assigns a frequency set to the node with the specified allowed accesses before deletion
     * @param set the frequency set to be assigned to the node
     * @param _accessesRemaing the accesses remaining to the frequency set before deletion
     */
    public void setFrequencySet(FrequencySet set, int _accessesRemaing){
        this.fset = set;
        this.accessesRemaining = _accessesRemaing;
    }
    
    /**
     * Getter of the node's frequency set
     * @return the frequency set assigned to this node
     */
    public FrequencySet getFrequencySet(){
        return this.fset;
    }
    
    /**
     * Decreases remaining accesses to the node's frequency set
     */
    public void decreaseRemaininAccesses(){
        this.accessesRemaining--;

        //if remaining accesses counter is zero, we can delete frequency set as
        //all decendants have used it
        if(this.accessesRemaining == 0){
            this.fset = null;
        }
    }
    
    /**
     * Sets the node if it is already processed or not
     * @param processed 
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    
    /**
     * Gets number of the generalization node's elements
     * @return number of the node's elements
     */
    public int size(){
        return nodeElements.length;
    }
    
    /**
     * Compares this node with another generalization node and finds their first
     * different elements
     * @param p the node to be compared with 
     * @return the number of the node element that is different
     */
    public int findDiffElement(GeneralizationNode p){
        GeneralizationNodeElement[] pElements = p.getNodeElements();
        for(int i=0; i<nodeElements.length; i++){
            if(!this.nodeElements[i].equals(pElements[i])){
                return i;
            }
        } 
        return 0;
    }
    
    /**
     * Gets a full name of the node as a string
     * @param columnNames a map with the names of the columns
     * @return a full name of the node
     */
    public String toStringFullName(Map<Integer, String> columnNames){
        String nodeName = "";
        for(GeneralizationNodeElement e : nodeElements){
            nodeName = nodeName + " " + columnNames.get(e.qid) + "(" + e.level + ")";
        }
        return "["+nodeName.trim()+"]";
    }
}
