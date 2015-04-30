/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.algorithms;

import edu.uci.ics.jung.graph.Graph;
import java.util.List;
import java.util.Map;
import privacytool.framework.algorithms.flash.LatticeNode;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.Hierarchy;

/**
 * The interface of the algorithm
 * @author serafeim
 */
public interface Algorithm {
    public void setDataset(Data dataset);
    public void setHierarchies(Map<Integer, Hierarchy> hierarchies);
    public void setArguments(Map<String, Integer> arguments);
    public Object anonymize();

    public Graph getLattice(Map<LatticeNode, Integer> levels, Map<LatticeNode, Integer> positions, 
            List<Integer> numNodesInLevel);
}
