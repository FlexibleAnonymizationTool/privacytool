/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package privacytool.framework.algorithms.flash;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import privacytool.framework.algorithms.Algorithm;
import privacytool.framework.algorithms.incognito.GeneralizationNode;
import privacytool.framework.algorithms.incognito.GeneralizationNodeElement;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.Hierarchy;

/**
 *
 * @author serafeim
 */
public class Flash implements Algorithm{
    Data dataset = null;
    Map<Integer, Hierarchy> hierarchies = null;
    Integer k = null;
    LatticeBuilder builder = null;
    Lattice lattice = null;
    int hierarchiesNum = -1;
    HistoryBuffers buffers = new HistoryBuffers(10);   
    Set<LatticeNode> resultset = new HashSet<>();
    
    @Override
    public void setDataset(Data dataset) {
        this.dataset = dataset;
    }
    
    @Override
    public void setHierarchies(Map<Integer, Hierarchy> hierarchies) {
        this.hierarchies = hierarchies;
    }
    
    @Override
    public void setArguments(Map<String, Integer> arguments) {
        this.k = arguments.get("k");
    }
    
    @Override
    public Object anonymize() {
        
        //determine min-max levels of quasi-ids
        hierarchiesNum = this.hierarchies.keySet().size();
        int qidColumns[] = new int[hierarchiesNum];
        int minLevels[] = new int[hierarchiesNum];
        int maxLevels[] = new int[hierarchiesNum];
        int distinctValues[][] = new int[hierarchiesNum][];
        int count = 0;
        
        for(Integer column : this.hierarchies.keySet()){
            Hierarchy h = this.hierarchies.get(column);
            
            //store QI columns and min-max levels
            qidColumns[count] = column;
            minLevels[count] = 0;
            maxLevels[count] = getHierarchyHeight(h)-1;
            
            //compute distinct values in hierarchy
            distinctValues[count] = new int[getHierarchyHeight(h)];
            findHierarchyDistinctValues(h, distinctValues[count]);
            count++;
        }
                
        //build lattice
        builder = new LatticeBuilder(qidColumns, minLevels, maxLevels);
        lattice = builder.build();
        Heap heap = new Heap(maxLevels, distinctValues);
        Sorting sorter = new Sorting(maxLevels, distinctValues);
        
        //outer loop of Flash algorithm
        for(int level = 0; level <= lattice.getHeight()-1; level++){
            for(LatticeNode node : sorter.sort(lattice.getLevels()[level])){
                if(!node.isTagged()){
                    LatticeNode[] path = findPath(node, maxLevels, distinctValues);
                    checkPath(path, heap);
                    while(!heap.isEmpty()){
                        node = heap.extractMin();
                        for(LatticeNode successor : sorter.sort(node.getSuccessors())){
                            if(!successor.isTagged()){
                                path = findPath(successor, maxLevels,distinctValues);
                                checkPath(path, heap);
                            }
                        }
                    }
               }
            }
        }
        System.out.println("Results : " + this.resultset);
        
        return this.resultset;
    }
    
    public void checkPath(LatticeNode[] path, Heap heap){
        int low = 0;
        int high = path.length-1;
        
        while(low <= high){
            int mid = (low + high) / 2;
            if((low + high) % 2 > 0)
                mid++;
            
            LatticeNode midNode = path[mid];
            if(checkAndTag(midNode)){
                high = mid - 1;
            }
            else{
                heap.add(midNode);
                low = mid + 1;
            }
        }
    }
    
    public LatticeNode[] findPath(LatticeNode node, int[] maxLevels, int[][] distinctValues){
        List<LatticeNode> path = new ArrayList<>();
        Sorting sorter = new Sorting(maxLevels, distinctValues);
        
         while(true){
             LatticeNode headNode = head(path);
             if(headNode != null && headNode == node)
                 break;

            path.add(node);

            for(LatticeNode upNode : sorter.sort(node.getSuccessors())){
                if(!upNode.isTagged()){
                    node = upNode;
                    break;
                }
            }
        }
        
        return path.toArray(new LatticeNode[path.size()]);
    }
    
    private LatticeNode head(List<LatticeNode> path){
        if(path.size() > 0)
            return path.get(path.size()-1);
        return null;
    }
    
    public boolean checkAndTag(LatticeNode node){
        Buffer curBuffer = null;

        LatticeNode bestNode = this.buffers.findClosestNode(node);
        
        if (bestNode != null){
            //buffer found in history buffers
//            System.out.println("node: " + node.toString() + " bestNode's buff: " + bestNode.toString());
            Buffer bestNodeBuffer = this.buffers.get(bestNode);
            
            curBuffer = new Buffer(this.dataset, this.hierarchies);
            curBuffer.compute(node, bestNode, bestNodeBuffer, this.lattice.getQidColumns());
            
        }
        else{
            //compute frequency set from dataset
            curBuffer = new Buffer(this.dataset, this.hierarchies);
            curBuffer.compute(node, this.lattice.getQidColumns());
            
        }
        
        //check if node is k-anonymous
        if(curBuffer.isKAnonymous(this.k)){
//            System.out.println(node.toString() + " is k" );
            lattice.setTagUpwards(node, this.resultset);
            return true;
        }
        else{
//            System.out.println(node.toString() + " is not k" );
            //put current buffer node to history
            this.buffers.put(node, curBuffer);
            
            lattice.setTagDownwards(node);
            return false;
        }
    }
        
    private int getHierarchyHeight(Hierarchy h){   
        if(h.getHierarchyType().equals("range")){
            return h.getHeight() + 1;   //as range's leaf level is not present in the hierarchy
        }
        
        return h.getHeight();
    }
    
    private void findHierarchyDistinctValues(Hierarchy h, int[] distinctValues){
        
        if(h.getHierarchyType().equals("distinct")){
            for(int level=0; level < distinctValues.length; level++){
                distinctValues[level] = h.getLevelSize(level);
//                System.out.println("level " + level + " size: " + distinctValues[level]);
            }
        }
        else{
            for(int level=1; level < distinctValues.length; level++){
                distinctValues[level] = h.getLevelSize(level-1);
            }
            
            //for the leaf nodes set the count of the rows
            distinctValues[0] = this.dataset.getDataLenght();
            
////            //print distinct values
//            for(int level=0; level < distinctValues.length; level++){
//                System.out.println("level " + level + " size: " + distinctValues[level]);
//            }
        }
    }
    
    @Override
    public Graph getLattice(Map<LatticeNode, Integer> levels, 
                                    Map<LatticeNode, Integer> positions, List<Integer> numNodesInLevel){
        
        LatticeNode[][] nodesArray = this.lattice.getLevels();
        
//        int[] qids = lattice.getQidColumns();
        Graph<LatticeNode, Integer> graph = new DirectedSparseGraph<>();
        int edgeNum = 0;
        for(int i=0; i<nodesArray.length; i++){
            
//            System.out.println("level " + i);
            
            numNodesInLevel.add(i, nodesArray[i].length);
            
            //sort nodes of level
            Arrays.sort(nodesArray[i], new Comparator<LatticeNode>() {
                @Override
                public int compare(LatticeNode node1, LatticeNode node2) {
                    int[] transformation1 = node1.getTransformation();
                    int[] transformation2 = node2.getTransformation();
                    
                    for(int i=0; i<transformation1.length; i++){
                        if(transformation1[i] < transformation2[i]){
                            return 1;
                        }
                        else if(transformation1[i] > transformation2[i]){
                            return -1;
                        }
                    }
                    return 0;
                }

            });
            
            for(int j=0; j<nodesArray[i].length; j++){
                
                LatticeNode curNode = nodesArray[i][j];
                
//                System.out.println(curNode);
                
                levels.put(curNode, i);
                positions.put(curNode, j);
                
                graph.addVertex(nodesArray[i][j]);
                LatticeNode[] successors = curNode.getSuccessors();
                for(LatticeNode suc : successors){
                    graph.addVertex(suc);
                    graph.addEdge(edgeNum, curNode, suc);
                    edgeNum++;
                }
            }
//            System.out.println();
        }
        
        return graph;
    }
}
