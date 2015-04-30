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

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import privacytool.framework.algorithms.Algorithm;
import privacytool.framework.algorithms.flash.Lattice;
import privacytool.framework.algorithms.flash.LatticeBuilder;
import privacytool.framework.algorithms.flash.LatticeNode;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.Hierarchy;

/**
 * Implementation of the Incognito Algorithm for k-anonymity
 * @author serafeim
 */
public class Incognito implements Algorithm{
    Data dataset = null;
    Map<Integer, Hierarchy> hierarchies = null;  
    GeneralizationGraph[][] graphs = null;
    Map<Integer, Set<GeneralizationNode>> resultset = new HashMap<>();
   
    //k parameter of k-anonymity
    Integer k = null;
    
    /**
     * Sets the dataset
     * @param _dataset the dataset to be used
     */
    @Override
    public void setDataset(Data _dataset) {
        dataset = _dataset;
    }

    /**
     * Sets the hierarchies
     * @param _hierarchies hierarchies to be used
     */
    @Override
    public void setHierarchies(Map<Integer, Hierarchy> _hierarchies) {
        hierarchies = _hierarchies;
    }

    /**
     * Anonymization process
     * @return 
     */
    @Override
    public Object anonymize() {
        
        //get all possible combinations of the hierarchies
        Set<Set<Integer>>[] combinations = getCombinations(hierarchies.keySet());
        graphs = new GeneralizationGraph[combinations.length][];
        
        PriorityQueue<QueueElement> queue = createQueue();
        
        int i;
        for(i=0; i<graphs.length; i++){
            
            Set<GeneralizationNode> resultsetInLevel = new LinkedHashSet<>();

            //generates graphs of this levels
            generateGraphsInLevel(i, combinations[i], resultset.get(i-1));
            
            //insert roots to queue
            for (int j=0; j<graphs[i].length; j++) {
                Set<GeneralizationNode> rootset = graphs[i][j].getRootset();
                for(GeneralizationNode root : rootset){
                    queue.add(new QueueElement(root, j));
                }
            }
               
            while(!queue.isEmpty()){
                
                //remove node from queue
                QueueElement queueElement = queue.remove();
                GeneralizationNode node = queueElement.getNode();
                int graphId = queueElement.getGraphId();
                
                //check if node has been already processed or marked
                if(graphs[i][graphId].getProcessed(node) || graphs[i][graphId].isMarked(node))
                    continue;
                graphs[i][graphId].setProcessed(node);
                
                //create a frequency set for the node
                FrequencySet frequencySet = new FrequencySet(dataset, hierarchies);
                
                //if node is root
                if(graphs[i][graphId].isRoot(node)){

                     //compute frequency set based on dataset
                    frequencySet.compute(node); 
                }
                else{
                    
                    //compute frequency set based on parent's frequency set
                    frequencySet.compute(node, node.findDiffElement(graphs[i][graphId].getParent(node)), 
                                        graphs[i][graphId].getFrequencySet(graphs[i][graphId].getParent(node)));
                     
                    //decrease remaining accesses to predecessor frequency set
                    for(GeneralizationNode predecessor : graphs[i][graphId].getPredecessors(node)){
                        graphs[i][graphId].decreaseRemaininAccesses(predecessor);
                    }
                }

                //assigning frequency set to node
                int successorsNum = graphs[i][graphId].getDirectGeneralizations(node).size();
                graphs[i][graphId].setFrequencySet(node, frequencySet, successorsNum);

                //check if node is k-anonymous
                boolean isKAnonymous = frequencySet.isKAnonymous(k);
                if(isKAnonymous){
                    
                    //if is anonymous mark direct generalizations
                    graphs[i][graphId].markDirectGeneralizations(node, resultsetInLevel);
                    
                    //and add them to result set
                    resultsetInLevel.add(node);
                }
                else{ 
                    
                    //insert direct generalizations to queue
                    Collection<GeneralizationNode> generalizations = graphs[i][queueElement.getGraphId()].getDirectGeneralizations(queueElement.getNode());
                    if(generalizations != null){

                        for(GeneralizationNode gn : generalizations){
                            QueueElement qEl = new QueueElement(gn, queueElement.getGraphId());
                            
                            //add to queue if not already 
                            if(!queue.contains(qEl)){
                                queue.add(qEl);
                            }
                        }
                    }
                }
            }
            resultset.put(i, resultsetInLevel);
            printResults(resultsetInLevel);
            
            if(i != graphs.length-1)
                graphs[i] = null;            
        }
        
        return convertResultset(resultset.get(i-1));
    }
        
    /**
     * Generates the graphs of one level
     * @param level the level
     * @param combinations the possible combinations of the level
     * @param prevLevelSet the result set of the previous level
     */
    private void generateGraphsInLevel(int level, Set<Set<Integer>> combinations,  Set<GeneralizationNode> prevLevelSet){
        
        graphs[level] = new GeneralizationGraph[combinations.size()];
        
        int j = 0;
        
        //create a graph for each combination
        for(Set<Integer> initSet : combinations){
            graphs[level][j] = createGraph(initSet, prevLevelSet);
            j++;
        }
    }
    
    /**
     * Creates a graph
     * @param innerSet the set of the initial node elements
     * @param prevLevelSet set of the previous level result set
     * @return a generalization graph
     */
    private GeneralizationGraph createGraph(Set<Integer> innerSet, Set<GeneralizationNode> prevLevelSet){
        
        GeneralizationGraph graph = new GeneralizationGraph(innerSet, prevLevelSet, hierarchies);
        graph.generateGraph(graph.getInitNode());
        graph.indexNodes();
        
        return graph;
    }
     
    /**
     * Creates a priority queue
     * @return the priority queue
     */
    private PriorityQueue<QueueElement> createQueue(){
        Comparator<QueueElement> comp = new Comparator<QueueElement>() {
            
            //sorts elements according to their height
            @Override
            public int compare(QueueElement o1, QueueElement o2) {
                if(o1.getNode().getNodeHeight() < o2.getNode().getNodeHeight())
                    return -1;
                else if (o1.getNode().getNodeHeight() > o2.getNode().getNodeHeight())
                    return 1;
                else 
                    return 0;
            }
        };
        PriorityQueue<QueueElement> queue = new PriorityQueue<>(5, comp);
        
        return queue;
    }
    
    private <T> Set<Set<T>>[] getCombinations(Set<T> initSet) {
        @SuppressWarnings("unchecked")
        Set<Set<T>>[] combinations = new HashSet[initSet.size()];
        for (Set<T> set2 : getPowerSet(initSet)) {
            int size = set2.size();
            if (size > 0) {
                Set<Set<T>> list = combinations[size - 1];
                if (list == null) {
                    list = new HashSet<Set<T>>();
                }
                list.add(set2);
                combinations[size - 1] = list;
            }
        }
        return combinations;
    }

    private <T> Set<Set<T>> getPowerSet(Set<T> set) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (set.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(set);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> tset : getPowerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(tset);
            sets.add(newSet);
            sets.add(tset);
        }
        return sets;
    }

    private void printResults(Set<GeneralizationNode> resultsetInLevel) {
        for(GeneralizationNode n : resultsetInLevel){
            System.out.println(n.toString());
        }
    }
    
    private Map linkResultsToMap(Set<GeneralizationNode> resultset){
        LinkedHashMap<GeneralizationNode, Collection<GeneralizationNode>> map = new LinkedHashMap<>();
        int levels = this.graphs.length;
        
        GeneralizationGraph graph = this.graphs[levels-1][0];
        
        
        for(GeneralizationNode node : resultset){
            map.put(node, graph.getDirectGeneralizations(node));
            System.out.println(node + " " +  graph.getDirectGeneralizations(node));
        }
        
        this.graphs[levels-1][0] = null;
        
        return map;
    }
    
    /**
     * Sets the appropriate arguments of the algorithm
     * @param arguments map of the algorithms
     */
    @Override
    public void setArguments(Map<String, Integer> arguments){
        this.k = arguments.get("k");
    
    }

    @Override
    public Graph getLattice(Map<LatticeNode, Integer> levels, Map<LatticeNode, Integer> positions, List<Integer> numNodesInLevel) {
        int hierarchiesNum = this.hierarchies.keySet().size();
        int qidColumns[] = new int[hierarchiesNum];
        int minLevels[] = new int[hierarchiesNum];
        int maxLevels[] = new int[hierarchiesNum];
        int count = 0;
        
        for(Integer column : this.hierarchies.keySet()){
            Hierarchy h = this.hierarchies.get(column);
            
            //store QI columns and min-max levels
            qidColumns[count] = column;
            minLevels[count] = 0;
            maxLevels[count] = getHierarchyHeight(h)-1;
            count++;
        }
        
        //build lattice
        LatticeBuilder builder = new LatticeBuilder(qidColumns, minLevels, maxLevels);
        Lattice lattice = builder.build();
        
        
        
        //convert lattice to jung graph
        LatticeNode[][] nodesArray = lattice.getLevels(); 
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
        }
        
        return graph;
    }

    private Set<LatticeNode> convertResultset(Set<GeneralizationNode> initset){
        Set<LatticeNode> newset = new HashSet<>();
        int nodeId = 0;
        
        for(GeneralizationNode node : initset){
            GeneralizationNodeElement[] elements = node.getNodeElements();
            int[] transformation = new int[elements.length];
            for(int i=0; i<elements.length; i++){
               transformation[i] = elements[i].level;
            }
            LatticeNode newNode = new LatticeNode(nodeId);
            nodeId++;
            newNode.setTransformation(transformation, -1);
            newset.add(newNode);
        }
        
        return newset;
    }
    
    private int getHierarchyHeight(Hierarchy h){   
        if(h.getHierarchyType().equals("range")){
            return h.getHeight() + 1;   //as range's leaf level is not present in the hierarchy
        }
        
        return h.getHeight();
    }
    
    
    private class QueueElement{
        GeneralizationNode node = null;
        int graphId = -1;
        
        public QueueElement(GeneralizationNode _node, int _graphId){
            node = _node;
            graphId = _graphId;
        }

        public GeneralizationNode getNode() {
            return node;
        }

        public int getGraphId() {
            return graphId;
        }
        
         @Override
        public String toString() {
            return this.node.toString() + this.graphId;        
        }
        
        @Override
        public boolean equals(Object obj){
            if(obj == this)
                return true;
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            QueueElement el = (QueueElement)obj;    
            return (this.node.equals(el.getNode()) && (this.graphId == el.getGraphId()));        
        } 
        
        @Override
        public int hashCode() {  
            return this.toString().hashCode();
        }
    }
}
