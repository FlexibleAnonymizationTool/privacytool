/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.algorithms.incognito;

/**
 * A node element
 * @author serafeim
 */
public class GeneralizationNodeElement {
    /**
     * The quasi identifier id
     */
    public int qid = -1;
    
    /**
     * The level of the QI's hierarchy tree described by this element
     */
    public int level = -1;
    
    /**
     * Constructor of the class
     * @param _qid a quasi identifier id 
     * @param _level the level of the QI's hierarchy tree
     */
    public GeneralizationNodeElement(int _qid, int _level){
        qid = _qid;
        level = _level;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        GeneralizationNodeElement e = (GeneralizationNodeElement) obj;
        return ((this.qid == e.qid) && (this.level == e.level));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.qid) ^ (Double.doubleToLongBits(this.qid) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.level) ^ (Double.doubleToLongBits(this.level) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return qid + "" + level; 
    }
}
