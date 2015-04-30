/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package privacytool.framework.algorithms.flash;

import java.util.Arrays;

/**
 *
 * @author serafeim
 */
public class LatticeNode {
    
    /** The id. */
    public final int id;
    /** The level. */
    private int level;
    /** The predecessors. */
    private LatticeNode[] predecessors;
    /** The upwards. */
    private LatticeNode[] successors;
    /** The down index. */
    private int preIndex;
    /** The up index. */
    private int sucIndex;
    /** The transformation. */
    private int[] transformation;
    
    private boolean tagged = false;
    
    /**
     * Instantiates a new node.
     *
     * @param id
     */
    public LatticeNode(int id) {
        this.id = id;
        this.preIndex = 0;
        this.sucIndex = 0;
    }
    /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#equals(java.lang.Object)
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final LatticeNode other = (LatticeNode) obj;
        return Arrays.equals(transformation, other.transformation);
    }

    /**
     * Returns the level.
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    public double getAvgGeneralization(int[] maxLevels){

        double sum = 0.0;
        
        for(int i=0; i < this.transformation.length; i++)
            sum += this.transformation[i] / (double) maxLevels[i];
        
        return sum / (double)this.transformation.length;
    }
    
    public double getDistinctValuesAvgGen(int[][] distinctValues) {
        double sum = 0.0;
        
        for(int i=0; i < this.transformation.length; i++){
            sum += distinctValues[i][this.transformation[i]] / (double)distinctValues[i][0];
        }
        
        sum /= this.transformation.length;
        
        return 1-sum;
    }
    /**
     * Returns the predecessors.
     *
     * @return
     */
    public LatticeNode[] getPredecessors() {
        return predecessors;
    }
    /**
     * Returns the successors.
     *
     * @return
     */
    public LatticeNode[] getSuccessors() {
        return successors;
    }
    /**
     * Returns the transformation.
     *
     * @return
     */
    public int[] getTransformation() {
        return transformation;
    }
    /*
    * (non-Javadoc)
    *
    * @see java.lang.Object#hashCode()
    */
    @Override
    public int hashCode() {
        return Arrays.hashCode(transformation);
    }

    /**
     * Sets the transformation.
     *
     * @param transformation
     * @param level
     */
    public void setTransformation(int[] transformation, int level) {
        this.transformation = transformation;
        this.level = level;
    }

    /**
     * Sets the predecessors.
     *
     * @param nodes
     */
    protected void setPredecessors(LatticeNode[] nodes) {
        predecessors = nodes;
    }

    /**
     * Sets the successors.
     *
     * @param nodes
     */
    protected void setSuccessors(LatticeNode[] nodes) {
        successors = nodes;
    }
    /**
     * Adds a predecessor.
     *
     * @param predecessor
     */
    void addPredecessor(LatticeNode predecessor) {
        predecessors[preIndex++] = predecessor;
    }
    /**
     * Adds a successor.
     *
     * @param successor
     */
    void addSuccessor(LatticeNode successor) {
        successors[sucIndex++] = successor;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.transformation);
    }
    
    public int[] getArray(){
       return this.transformation;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void Tag() {
        this.tagged = true;
    }



}
