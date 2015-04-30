/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.algorithms.incognito;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import privacytool.framework.hierarchy.Hierarchy;

/**
 * A generalization graph
 * @author serafeim
 */
public class GeneralizationGraph {
    Integer edgeNum = 0;
    Graph<GeneralizationNode, Integer> g = new DirectedSparseMultigraph<>();
    Set<GeneralizationNode> rootset = null;
    GeneralizationNode initNode = null;
    Map<String, GeneralizationNode> nodesMap = null;
    Set<GeneralizationNode> prevLevelSet = null;
    Map<Integer, Hierarchy> hierarchies = null;
    
    public GeneralizationGraph(Set<Integer> initSet, Set<GeneralizationNode> _prevLevelSet, 
                                                Map<Integer, Hierarchy> _hierarchies) {
        prevLevelSet = _prevLevelSet;
        hierarchies = _hierarchies;
        
        //set root node
        GeneralizationNodeElement[] nodeElements = new GeneralizationNodeElement[initSet.size()];
        
        int i = 0;
        for(Integer qid : initSet){
            nodeElements[i] = new GeneralizationNodeElement(qid, 0);
            i++;
        } 
   
        initNode = new GeneralizationNode(nodeElements);
    }

    
    /**
     * Generates recursively the sub-graph with a starting point and further
     * @param node the starting point of the sub-graph
     */
    public void generateGraph(GeneralizationNode node){
        
        List<GeneralizationNode> tempList = new ArrayList<>();
        
        //create the next node's elements
        for(GeneralizationNodeElement e : node.getNodeElements()){

            if(e.level+1 < getHierarchyHeight(hierarchies.get(e.qid))){
                
                //create a copy of the elements of the previous node
                GeneralizationNodeElement[] newNodeElements = new GeneralizationNodeElement[node.getNodeElements().length];
                for(int i=0; i<node.getNodeElements().length; i++){
                    GeneralizationNodeElement element = node.getNodeElements()[i];
                    newNodeElements[i] = new GeneralizationNodeElement(element.qid, element.level);
                }
                
                //search to change the level of the new node's appropriate element
                for(GeneralizationNodeElement elementToChange : newNodeElements){
                    if(elementToChange.equals(e)){
                        elementToChange.level++;
                    }
                }
                GeneralizationNode newNode = new GeneralizationNode(newNodeElements);
                tempList.add(newNode);
            }
        }
        
//        if(tempList.isEmpty()){
//            return;
//        }
        
        //if this node is valid based on previous level resultset
        if(checkValidity(node)){
            //add node to graph and link with child nodes
            linkNodes(node, tempList);
        }
            
        //call the same method recursively for the new nodes
        for(GeneralizationNode n : tempList){
            generateGraph(n);
        }
    }

    /**
     * Getter of the theoretical initial node of the graph
     * @return the initial node
     */
    public GeneralizationNode getInitNode() {
        return initNode;
    }

    /**
     * Links a certain node with its descendants
     * @param node the parent node
     * @param tempList a list of the child nodes
     */
    private void linkNodes(GeneralizationNode node, List<GeneralizationNode> tempList) {
        
        g.addVertex(node);
        for(GeneralizationNode destNode : tempList){
            
            g.addVertex(destNode);
            
            //if node not already exists
            if(!g.isSuccessor(node, destNode)){
                g.addEdge(edgeNum, node, destNode, EdgeType.DIRECTED);
                edgeNum++;
            }
        }
    }
    
    /**
     * Checks if given node is a valid node based on the previous level result set
     * @param node the node to be checked
     * @return true if node is valid, false otherwise
     */
    private boolean checkValidity(GeneralizationNode node){
        
        //if this is the first level, node is valid
        if(this.prevLevelSet == null){
            return true;
        }
        //else we have to check validity
        else{
            boolean isValid = true;
            
            //get all posible combinations of the node elements
            Set<Set<GeneralizationNodeElement>> combinations = getCombinations(node.getNodeElementsSet());
//            System.out.println(node.toString() + " -> " + combinations);
            for(Set<GeneralizationNodeElement> comb : combinations){
                GeneralizationNodeElement[] nodeElementsArray = comb.toArray(new GeneralizationNodeElement[comb.size()]);
                GeneralizationNode checkNode = new GeneralizationNode(nodeElementsArray);
                
                //check if this combination is contained in the previous level result set
                if(!this.prevLevelSet.contains(checkNode)){
                    isValid = false;
                    break;
                }
            }
            return isValid;
        }
    }
    
    /**
     * Gets height of the given hierarchy
     * @param h an hierarchy
     * @return hierarchy's height
     */
    private int getHierarchyHeight(Hierarchy h){
        
        if(h.getHierarchyType().equals("range")){
            return h.getHeight() + 1;   //as range's leaf level is not present in the hierarchy
        }
        
        return h.getHeight();
    }
    
    /**
     * Getter of the root elements of the graph
     * @return set with the root elements (vertices with no incoming edges)
     */
    public Set<GeneralizationNode> getRootset(){
        
        //find rootset for first time
        if(rootset == null){
            rootset = new HashSet<>();
            for (GeneralizationNode node : g.getVertices()){
                
                //roots are considered vertices with no incoming edges
                if (g.inDegree(node) == 0){
                    rootset.add(node);
                }
            }
        }
        
        return rootset;
    }
        
    /**
     * Getter of the direct generalizations of a specific node in the graph
     * @param node a graph's generalization node
     * @return the direct generalizations of node in the graph
     */
    public Collection<GeneralizationNode> getDirectGeneralizations(GeneralizationNode node){
        return g.getSuccessors(node);
    }
    
    /**
     * Print all vertices of the graph
     */
    public void printVertices(){
        Collection<GeneralizationNode> nodes = g.getVertices();
        for(GeneralizationNode n : nodes){
            System.out.println(n.toString() + " " + n.isProcessed());
        }
    }
    
    /**
     * Creates a set with the graph's vertices
     */
    public void indexNodes(){
        nodesMap = new HashMap<>();
        Collection<GeneralizationNode> nodes = g.getVertices();
        for(GeneralizationNode n : nodes){
            nodesMap.put(n.toString(), n);
        }
    }
    
    private <T> Set<Set<T>> getCombinations(Set<T> initSet) {
        @SuppressWarnings("unchecked")
        Set<Set<T>> results = new HashSet<>();
        Set<Set<T>> combinations = getPowerSet(initSet);
        for(Set<T> comb : combinations){
            if(comb.size() != initSet.size()-1)
                continue;
            results.add(comb);
        }
        return results;
    }

    private <T> Set<Set<T>> getPowerSet(Set<T> set) {
        Set<Set<T>> sets = new LinkedHashSet<Set<T>>();
        if (set.isEmpty()) {
            sets.add(new LinkedHashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(set);
        T head = list.get(0);
        Set<T> rest = new LinkedHashSet<T>(list.subList(1, list.size()));
        for (Set<T> tset : getPowerSet(rest)) {
            Set<T> newSet = new LinkedHashSet<T>();
            newSet.add(head);
            newSet.addAll(tset);
            sets.add(newSet);
            sets.add(tset);
        }
        return sets;
    }
    
    /**
     * Sets that the specified node has been processed
     * @param node a generalization node
     */
    public void setProcessed(GeneralizationNode node){
        nodesMap.get(node.toString()).setProcessed(true);
    }
    
    /**
     * Gets if the specified element has been processed
     * @param node a generalization node
     * @return true if has been processed, false otherwise
     */
    public boolean getProcessed(GeneralizationNode node){
        return nodesMap.get(node.toString()).isProcessed();
    }

    /**
     * Marks a generalization node
     * @param node the node to be marked
     */
    public void mark(GeneralizationNode node){
        nodesMap.get(node.toString()).mark();
    }
    
    /**
     * Gets if specified node is marked
     * @param node a generalization node
     * @return true if node is marked, false otherwise
     */
    public boolean isMarked(GeneralizationNode node){
        return nodesMap.get(node.toString()).isMarked();
    }
    
    /**
     * Gets all graph's nodes
     * @return a collection with the graph nodes
     */
    public Collection<GeneralizationNode> getAllNodes(){
        return g.getVertices();
    }
    
    /**
     * Checks if specified node is in the result set
     * @param node a generalization node
     * @return true if node is present in the result set, false otherwise
     */
    public boolean isRoot(GeneralizationNode node){
        return rootset.contains(node);
    }
    
    /**
     * Associates a frequency set with a node
     * @param node a generalization node
     * @param set the frequency set to be assigned to the node
     * @param accessesRemaing the accesses remaining for the frequency before deletion
     */
    public void setFrequencySet(GeneralizationNode node, FrequencySet set, int accessesRemaing){
        nodesMap.get(node.toString()).setFrequencySet(set, accessesRemaing);
    }
    
    /**
     * Gets frequency set of a specified node
     * @param node a generalization node
     * @return the frequency set assigned to the node
     */
    public FrequencySet getFrequencySet(GeneralizationNode node){
        return nodesMap.get(node.toString()).getFrequencySet();
    }
    
    /**
     * Decreases remaining accesses to the frequency set of the specified node
     * @param node a generalization node
     */
    public void decreaseRemaininAccesses(GeneralizationNode node) {
        nodesMap.get(node.toString()).decreaseRemaininAccesses();
    }
    
    /**
     * Gets a parent of the specified node
     * @param node a generalization node
     * @return 
     */
    public GeneralizationNode getParent(GeneralizationNode node){
        GeneralizationNode parent = null;
        for(GeneralizationNode curNode : g.getPredecessors(node)){
            parent = curNode;
        }
        return parent;
    }

    /**
     * Get all predecessors of the specified node
     * @param node a generalization node
     * @return a collection with the predecessors of node
     */
    public Collection<GeneralizationNode> getPredecessors(GeneralizationNode node){
        return g.getPredecessors(node);
    }
    
    /**
     * Mark all direct generalizations of the specified node and adds them to result set
     * @param node a generalization node
     * @param results the result set
     */
    public void markDirectGeneralizations(GeneralizationNode node, Set<GeneralizationNode> results) {
        Collection<GeneralizationNode> generalizations = getDirectGeneralizations(node);
        if(generalizations == null)
            return;
        
        //mark all direct generalizations add them to result set
        for(GeneralizationNode gen : generalizations){
            mark(gen);
            results.add(gen);
            markDirectGeneralizations(gen, results);
        }
    }

}
