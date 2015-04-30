/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package privacytool.framework.algorithms.flash;

import cern.colt.Arrays;
import java.util.Set;

/**
 *
 * @author serafeim
 */
public class Lattice {
    
    private final int[] qidColumns;
    /** The levels. */
    private final LatticeNode[][] levels;
    /** The size. */
    private final int size;
    /**
     * Initializes a lattice.
     *
     * @param qidColumns
     * @param levels the levels
     * @param numNodes the max levels
     */
    public Lattice(final int[] qidColumns, final LatticeNode[][] levels, final int numNodes) {
        this.qidColumns = qidColumns;
        this.levels = levels;
        this.size = numNodes;
    }
    /**
     * Returns the bottom node.
     *
     * @return
     */
    public LatticeNode getBottom() {
        for (int i = 0; i<levels.length; i++) {
            if (levels[i].length==1){
                return levels[i][0];
            } else if (levels[i].length > 1) {
                throw new RuntimeException("Multiple bottom nodes!");
            }
        }
        throw new RuntimeException("Empty lattice!");
    }
    /**
     * Returns all levels in the lattice.
     *
     * @return
     */
    public LatticeNode[][] getLevels() {
        return levels;
    }
    /**
     * Returns the number of nodes in the lattice.
     *
     * @return
     */
    public int getSize() {
        return size;
    }
    /**
     * Returns the top node.
     *
     * @return
     */
    public LatticeNode getTop() {
        for (int i = levels.length - 1; i>=0; i--) {
            if (levels[i].length==1){
                return levels[i][0];
            } else if (levels[i].length > 1) {
                throw new RuntimeException("Multiple top nodes!");
            }
        }
        throw new RuntimeException("Empty lattice!");
    }

    /**
     * Sets the property to all predecessors of the given node.
     *
     * @param node the node
     */
    public void setTagDownwards(LatticeNode node) {
        node.Tag();
        for(LatticeNode predecessor : node.getPredecessors()){
            setTagDownwards(predecessor);
        }
    }
      
    /**
     * Sets the property to all successors of the given node.
     *
     * @param node the node
     * @param resultset
     */
    public void setTagUpwards(LatticeNode node, Set<LatticeNode> resultset) {
        
        node.Tag();
        resultset.add(node);
        for(LatticeNode successor : node.getSuccessors()){
            setTagUpwards(successor, resultset);
        }
    }
    
    public void print(){
        System.out.println("Lattice for columns : " + Arrays.toString(this.qidColumns));
        for(int i = 0; i<levels.length; i++){
            System.out.println("level " + i);
            for(int j = 0; j<levels[i].length; j++){
                System.out.println(levels[i][j] + " " + levels[i][j].isTagged());
            }
            System.out.println();
        }
    }
    
    public int getHeight(){
        return levels.length;
    }

    public int[] getQidColumns() {
        return qidColumns;
    }
    
    
}