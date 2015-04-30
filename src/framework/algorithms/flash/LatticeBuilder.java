/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package privacytool.framework.algorithms.flash;

/**
 *
 * @author serafeim
 */
public class LatticeBuilder {
    
    private int[] qidColumns = null;
    /** The curLevels. */
    private LatticeNode[][] levels = null;
    /** The maxLevels. */
    private int[] maxLevels = null;
    /** The minLevels. */
    private int[] minLevels = null;
    /**
     * Instantiates a new lattice builder.
     *
     * @param qidColumns
     * @param maxLevels the maxLevels
     * @param minLevels the minLevels
     */
    public LatticeBuilder(final int[] qidColumns, final int[] minLevels, final int[] maxLevels) {
        this.qidColumns = qidColumns;
        this.maxLevels = maxLevels;
        this.minLevels = minLevels;
    }
    /**
     * Builds the lattice
     *
     * @return the lattice
     */
    public Lattice build() {
        final int numNodes = buildLevelsAndMap();
        return new Lattice(qidColumns, levels, numNodes);
    }
    /**
     * Builds the curLevels and map.
     *
     * @return total number of nodes
     */
    private int buildLevelsAndMap() {
        // Init
        final int numQIs = maxLevels.length;
        int numNodes = 1;
        final int[] offsets = new int[numQIs];
        final int[] maxIndices = new int[numQIs];
        int maxLevel = 1;
        int id = 0;
        
        // Step 1
        for (int i = 0; i < numQIs; i++) {
            final int curMaxGeneralizationHeight = maxLevels[i] + 1;
            offsets[i] = numNodes;
            numNodes *= (curMaxGeneralizationHeight - minLevels[i]);
            maxLevel += (curMaxGeneralizationHeight - 1);
            maxIndices[i] = curMaxGeneralizationHeight - 1;
        }
        
        // Step 2
        final int[] levelsize = new int[maxLevel];
        final LatticeNode[] nodeArray = new LatticeNode[numNodes];
        for (int i = 0; i < nodeArray.length; i++) {
            nodeArray[i] = new LatticeNode(id++);
        }
            
        // Step 3
        for (int count = 0; count < numNodes; count++) {
            final int[] state = new int[numQIs];
            int tempCount = count;
            int level = 0;
            int numUpwards = 0;
            int numDownwards = 0;
            for (int i = state.length - 1; i >= 0; i--) {
                state[i] = (tempCount / offsets[i]) + minLevels[i];
                tempCount -= (state[i] - minLevels[i]) * offsets[i];
                level += (state[i]);
                if (state[i] < maxIndices[i]) {
                    numUpwards++;
                }
                if (state[i] != minLevels[i]) {
                    numDownwards++;
                }
            }
            final LatticeNode node = nodeArray[count];
            node.setTransformation(state, level);
            node.setPredecessors(new LatticeNode[numDownwards]);
            node.setSuccessors(new LatticeNode[numUpwards]);
            levelsize[level]++;
        }
        
        // Generate level arrays
        final LatticeNode[][] curLevels = new LatticeNode[maxLevel][];
        for (int i = 0; i < curLevels.length; i++) {
            curLevels[i] = new LatticeNode[levelsize[i]];
        }
        
        // Generate up and down links and initialize curLevels
        for (int i = 0; i < nodeArray.length; i++) {
            final LatticeNode node = nodeArray[i];
            final int level = node.getLevel();
            --levelsize[node.getLevel()];
            final int index = (curLevels[level].length - 1 - levelsize[level]);
            curLevels[level][index] = node;
            final int[] key = node.getTransformation();
            for (int j = 0; j < key.length; j++) {
                if (key[j] < maxIndices[j]) {
                    final int plusIndex = i + offsets[j];
                    final LatticeNode reachableNode = nodeArray[plusIndex];
                    node.addSuccessor(reachableNode);
                    reachableNode.addPredecessor(node);
                }
            }
        }
        
        // Finalize
        this.levels = curLevels;
        return numNodes;
    }
}
