/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.hierarchy.distinct;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import privacytool.framework.data.Data;
import privacytool.framework.dictionary.Dictionary;
import privacytool.framework.hierarchy.Hierarchy;
import privacytool.framework.hierarchy.NodeStats;

/**
 *
 * @author serafeim
 */
public class HierarchyImplDouble implements Hierarchy<Double> {
    String inputFile = null;
    String name = null;
    String nodesType = null;
    String hierarchyType = null;
    int height = -1;
    BufferedReader br = null;
    Double root = null;

    Map<Double, List<Double>> children = new HashMap<>();
    Map<Double, NodeStats> stats = new HashMap<>();
    Map<Double, Double> parents = new HashMap<>();
//    Map<Double, List<Double>> siblings = new HashMap<>();
    Map<Integer, ArrayList<Double>> allParents = new HashMap<>();
    
 
    
    public HierarchyImplDouble(String inputFile){
        this.inputFile = inputFile;
    }
    
    public HierarchyImplDouble(String _name, String _nodesType, String _hierarchyType){
        this.name = _name;
        this.nodesType = _nodesType;
        this.hierarchyType = _hierarchyType;
    }
    
    @Override
    public void load(){
        try {
            br = new BufferedReader(new FileReader(this.inputFile));
            processingMetadata();
            loadHierarchy();
            findAllParents();
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HierarchyImplDouble.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HierarchyImplDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(" i am here");
        //findAllParents();
    }

    private void loadHierarchy() throws IOException{
        String line;
        int curLevel = this.height - 1;
        
        while ((line = br.readLine()) != null) {
            String tokens[] = line.split(" ");
            if(line.trim().isEmpty()){
                curLevel--;
                continue;
            }
            boolean isChild = false;
            List<Double> ch = new ArrayList<>();
            for (String token : tokens){
                if(token.equals("has")){ 
                    isChild = true;
                    continue;
                }
                if(isChild){
                    //System.out.println(token);
                    ch.add(Double.parseDouble(token));
                    this.stats.put(Double.parseDouble(token), new NodeStats(curLevel));
                    this.parents.put(Double.parseDouble(token), Double.parseDouble(tokens[0]));  
                }
                else{
                    this.stats.put(Double.parseDouble(token), new NodeStats(curLevel-1));
                    
                    //level 0 and isChild == false then set as root 
                    if(curLevel - 1 == 0){
                        root = new Double(tokens[0]);
                    }
                }
                
                //System.out.println(token + ": " + isChild + " "  + curLevel);
               
            }
            this.children.put(Double.parseDouble(tokens[0]), ch);
            
//            set siblings
//            for(Double child : ch){
//                List<Double> sib = new ArrayList<>(ch);
//                sib.remove(child);
//                this.siblings.put(child, sib);
//            }
        }
    }
    
    private void processingMetadata() throws IOException{

        String line;
        while ((line = br.readLine()) != null) {
            //System.out.println(line);
            if(line.trim().isEmpty())
                break;

            //System.out.println("Metadata: " + line);
            String[] tokens = line.split(" ");
            if(tokens[0].equalsIgnoreCase("name")){
                this.name = tokens[1];
            }
            else if(tokens[0].equalsIgnoreCase("type")){
                this.nodesType = tokens[1];
            }
            else if(tokens[0].equalsIgnoreCase("height")){
                this.height = Integer.parseInt(tokens[1]);
            }
        }   
    }
    

    public void findAllParents(){
        List<Double> tempChild = null;
        int i = 0;
        int level = 0;
        ArrayList<Double> tempArr1 = new ArrayList<>();
        ArrayList<Double> tempArr2 = new ArrayList<>();
        
        tempArr1.add(root);
        allParents.put(level, tempArr1);
        tempArr2 = (ArrayList<Double>) tempArr1.clone();
        level ++;
        
        while (level <= height - 1 ){
            tempArr1 = new ArrayList<>();
            for (Double x : tempArr2) {
                tempChild = children.get(x);
                if ( tempChild != null){
                    for ( i = 0 ; i < tempChild.size() ; i ++ ){
                        tempArr1.add(tempChild.get(i));
                    }
                }
            }           
            allParents.put(level, tempArr1);
            tempArr2 = (ArrayList<Double>) tempArr1.clone();
            level ++;  
        }
    }
    

    public List<Double> getChildren(Double parent){
        return this.children.get(parent);
    }
    

    public Integer getLevel(Double node){
        return this.stats.get(node).getLevel();
    }
    

    public Double getParent(Double node){
        return this.parents.get(node);
    }
    

//    public List<Double> getSiblings(Double node) {
//        return this.siblings.get(node);
//    }  
    

    public int[][] getHierarchy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public void setHierarchy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public int getHierarchyLength() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public Double getRoot(){
        return root;
    }
   
    public String getName(){
        return this.name;
    }


    public Map<Integer, ArrayList<Double>> getAllParents() {
        return allParents;
    }


    @Override
    public void export(String file) {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("distinct");
            writer.println("name " + this.name);
            writer.println("type " + this.nodesType);
            writer.println("height " + this.height);
            writer.println();

            //write parents - childen to file
            for(int curLevel = height - 2; curLevel >= 0; curLevel--){
                //System.out.println("i = " + curLevel + "\t children = " + this.allParents.get(curLevel).toString() );
                for (Double curParent : this.allParents.get(curLevel)){
                    if(this.getChildren(curParent) == null){
                        continue;
                    }
                    if(this.getChildren(curParent).isEmpty())
                        continue;
                    StringBuilder sb = new StringBuilder();
                    for (Double child : this.getChildren(curParent)){
                        sb.append(child);
                        sb.append(" ");
                    } 
                    writer.println(curParent + " has " + sb.toString());
                }

                writer.println();
                
            }
            writer.close(); 
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(HierarchyImplDouble.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public String getNodesType() {
        return nodesType;
    }


    @Override
    public void add(Double newObj, Double parent) {
        System.out.println("add () newItem: "  + newObj.toString() + " parentItem: " + parent.toString());
        if(parent != null){
            this.stats.put(newObj, new NodeStats(this.stats.get(parent).getLevel()+1));
            this.parents.put(newObj, parent);
            
            //add siblings
//            List<Double> cList = this.children.get(parent);
//            List<Double> sibs = new ArrayList<>();
//            if(cList != null)
//                sibs.addAll(cList);
//            this.siblings.put(newObj, sibs);
//            for(Double sib : sibs){
//                this.siblings.get(sib).add(newObj);
//            }
            
            this.children.put(newObj, new ArrayList<Double>());
            if(this.children.get(parent) != null){
                this.children.get(parent).add(newObj);
            }else{
                ArrayList<Double> c = new ArrayList<>();
                c.add(newObj);
                this.children.put(parent, c);
            }
            //System.out.println(height + " " + this.levels.get(parent));
            
            ArrayList<Double> parentsInLevel = this.allParents.get(this.stats.get(parent).getLevel());
            //parent is a leaf node
            System.out.println(parentsInLevel);
            if(isLeafLevel(parentsInLevel)){
                ArrayList<Double> p = new ArrayList<>();
                p.add(parent);
                this.allParents.put(this.stats.get(parent).getLevel(), p);
                this.height++;
                //System.out.println("put parent " + parent.toString() + " to level " + this.stats.get(parent).getLevel() + " " + allParents);
            }else{
                if(!parentsInLevel.contains(parent)){
                    //System.out.println("parent PUT TO ORDER");
                    parentsInLevel.add(parent);
                }
            }
            
        }
        else{
            System.out.println("Error: parent is null!");
        }
    }

    private boolean isLeafLevel(ArrayList<Double> parentsInLevel){
        boolean  leafLevel = true;
        
        for (Double parent : parentsInLevel){
            List<Double> ch = this.children.get(parent);
            if(ch != null && ch.size() > 0){
                leafLevel = false;
                break;
            }
        }
        return leafLevel;
    }

    @Override
    public Map<Integer, Set<Double>> remove(Double item)
    {
        Map<Integer, Set<Double>> nodesMap = BFS(item,null);
        for(Integer i = nodesMap.keySet().size() ; i > 0 ; i--)
        {
            System.out.println(i + "-> " + nodesMap.get((i)));
            
            for(Double itemToDelete : nodesMap.get(i)){
                //System.out.println(itemToDelete.toString());
                if(itemToDelete.equals(root)){
                    System.out.println("Cannot remove root");
                    return null;
                }
                children.remove(itemToDelete);
                children.get(getParent(itemToDelete)).remove(itemToDelete);
                parents.remove(itemToDelete);
//                List<Double> sibs = siblings.get(itemToDelete);
//                if(sibs != null){
//                    for(Double sib : sibs){
//                        if(siblings.get(sib) != null){
//                            siblings.get(sib).remove(itemToDelete);
//                        }
//                    }
//                }
//                siblings.remove(itemToDelete);
                
                         
                List<Double> p = allParents.get(stats.get(itemToDelete).level);
                p.remove(itemToDelete);
                if(p.isEmpty()){
                    allParents.remove(stats.get(itemToDelete).level);
                    height--;
                }
                stats.remove(itemToDelete);
            }
            
        }
        return nodesMap;
    }

    
    

    @Override
    public void clear() {
        //System.out.println("model clear");
        children = new HashMap<>();
        stats = new HashMap<>();
        parents = new HashMap<>();
//        siblings = new HashMap<>();
        allParents = new HashMap<>();
        children.put(root, new ArrayList<Double>());
        stats.put(root, new NodeStats(0));
        //System.out.println("model : " + root.toString());
        /*
        ArrayList<Object> tList = new ArrayList<>();
        tList.add(root);
        allParents.put(0, tList);
        */
        height = 1;
    }

    

    @Override
    public void edit(Double oldValue, Double newValue){
        //update children map 
        List<Double> childrenList = this.children.get(oldValue);
        if(childrenList != null){
            this.children.put(newValue, childrenList);
        }
        this.children.remove(oldValue); 
        
        if(this.getParent(oldValue) != null){
            int index = this.children.get(this.getParent(oldValue)).indexOf(oldValue);
            this.children.get(this.getParent(oldValue)).set(index, newValue);
            
            //update parents map 
            Double parent = this.parents.get(oldValue);

            this.parents.remove(oldValue);
            this.parents.put(newValue, parent);

            
        }
        
        if(childrenList != null){
            for(Double child : childrenList){
                    this.parents.put(child, newValue);
            }
        }
        
//        if(this.getSiblings(oldValue) != null){
//            List<Double> mySiblings = this.siblings.get(oldValue);
//            this.siblings.remove(oldValue);
//            this.siblings.put(newValue, mySiblings);
//            for (Double sib : mySiblings){
//                System.out.println("sibling : " + sib);
//                int i = this.siblings.get(sib).indexOf(oldValue);
//                System.out.println(i);
//                if(i != -1){        //TODO: fix this! 
//                    this.siblings.get(sib).set(i, newValue); 
//                }
//                           
//            }
//        }
        
        //update allParents
        ArrayList<Double> parentsInLevel = this.allParents.get(this.stats.get(oldValue).getLevel());
        
        if(parentsInLevel != null){
            int i = parentsInLevel.indexOf(oldValue);
            if(i != -1){         //parent not found
                System.out.println("to i : " + i + " oldvalue : " + oldValue.toString());
                parentsInLevel.set(i, newValue);
            }
        }
        
        //update levels
        this.stats.put(newValue, this.stats.get(oldValue));
        this.stats.remove(oldValue);
    }


    @Override
    public boolean contains(Double o){  
        return stats.get(o) != null;
    }

    @Override
    public Map<Integer,Set<Double>> dragAndDrop(Double firstObj, Double lastObj) {
        Double parentFirstObj ;
        ArrayList childs1 = null;
        ArrayList childs2 = null;
        ArrayList simb1 = null;
        ArrayList simb2 = null;
        ArrayList parents = null;
        int levelFirstObj;
        int levelLastObj;
        int levelObj;
        int newLevel;
        Set<Double> s = null;
        NodeStats nodeStat = null;
        
        
        Map<Integer,Set<Double>> m = this.BFS(firstObj,lastObj);
        
        if ( m != null ){
        
            //afairw to prwto komvo apo ta paidia(children) tou patera tou
            parentFirstObj = this.getParent(firstObj);
            childs1 = (ArrayList) this.getChildren(parentFirstObj);
            for( int i = 0 ; i < childs1.size() ; i ++ ){
             
       if ( childs1.get(i).equals(firstObj)){
                    childs1.remove(i);
                    break;
                }
            }

            
            //allazw ton parent tou prwtou paidiou
            this.parents.put(firstObj,lastObj);


            //afairw ta siblings tou prwtou komvou
//            this.siblings.remove(firstObj);
            

            //topothetw to prwto komvo sta swsta siblings
//            childs1 = (ArrayList) this.getChildren(lastObj);
//            simb1 = null;
//            if (childs1 != null ){
//                simb1 = new ArrayList<Double>();
//                for ( int i = 0 ; i < childs1.size() ; i++ ){
//                    simb1.add(childs1.get(i));
//                    simb2 = (ArrayList)this.getSiblings((Double) childs1.get(i));
//                    if ( simb2 != null){
//                        simb2.add(firstObj);
//                    }
//                    else{
//                        simb2 = new ArrayList<Double>();
//                    }
//                }
//            }
//            this.siblings.put(firstObj, simb1);

            
            //vazw ton prwto komvo sta paidia(children) tou deuterou komvou
            if ( this.getChildren(lastObj) != null ){
                this.getChildren(lastObj).add(firstObj);
            }
            else{
                childs1 = new ArrayList<Double>();
                childs1.add(firstObj);
                this.children.put(firstObj, childs1);
            }


            //diagrafw to allparents tou prwtou kai tou dentrou tou
            for ( int i = 1 ; i <= m.size() ; i ++ ){
                s = m.get(i);
                for( Double node : s ){
                    levelObj = this.getLevel(node);
                    parents = this.allParents.get(levelObj);
                    System.out.println(levelObj + " kai " + parents);
                    for( int j = 0 ; j < parents.size() ; j++ ){
                        if ( parents.get(j).equals(node)){
                            parents.remove(j);
                        }
                    }
                }
            }
            

            //topothetw ton prwto komvo kai to dentro tou sto allparents
            levelLastObj = this.getLevel(lastObj);
            simb2 = this.allParents.get(levelLastObj);
            simb2.add(lastObj);
            newLevel = levelLastObj;
            for ( int i = 1 ; i <= m.size() ; i ++ ){
                s = m.get(i);
                 newLevel = newLevel + 1;
                if ( newLevel > this.allParents.size() - 1){// giati to allParents arxizei apo to miden
                    parents = new ArrayList<Double>();
                    parents.addAll(s);
                    this.allParents.put(newLevel,parents);
                    parents = null;
                }
                else{
                    parents = this.allParents.get(newLevel);
                    parents.addAll(s);
                    parents = null;
                }  
            }


            //allazw ta level tou dentrou tou prwtou komvou
            levelFirstObj =  this.getLevel(firstObj);
            levelLastObj = this.getLevel(lastObj);
            newLevel = levelLastObj + 1;
            nodeStat = this.stats.get(firstObj);
            nodeStat.level = newLevel;

            for ( int i = 2 ; i <= m.size() ; i ++  ){
                newLevel = newLevel + 1;
                s = m.get(i);
                for (Double d : s){
                    nodeStat = this.stats.get(d);
                    nodeStat.level = newLevel;
                }
            }
            
        }
        
        return m;
    }
    
    
    @Override
    public Integer getHeight() {
        return this.height;
    }
    
    
    @Override
    public Map<Integer,Set<Double>> BFS(Double firstnode,Double lastNode){
        Map<Integer,Set<Double>> bfsMap = new HashMap<Integer,Set<Double>>();
        LinkedList<Double> listNodes = new LinkedList<Double>();
        ArrayList childs1 = null;
        int counter = 1;
        int levelNode1;
        int levelNode2;
        Set s = new HashSet<Double>();
        
        
        s.add(firstnode);
        bfsMap.put(counter,s);
        listNodes.add(firstnode);
        counter ++;
        levelNode1 = this.getLevel(firstnode);
        
        while (!listNodes.isEmpty()){
            childs1 = (ArrayList) this.getChildren(listNodes.getFirst());
            if ( childs1 != null && !childs1.isEmpty()){// ean exei paidia
                levelNode2 = this.getLevel((Double) childs1.get(0));
                if (levelNode2 == levelNode1){// ean einai sto idio epipedo tote valta sto proigoumeno set
                    s.addAll(childs1);
                    if ( lastNode != null){
                        if (s.contains(lastNode)){
                            bfsMap = null;
                            break;
                        }
                    }
                    bfsMap.put(counter, s);
                }
                else{// ean den einai sto idio epipedo dimiourgise kainourgio set
                    s = new HashSet<Double>();
                    levelNode1 = levelNode2;
                    s.addAll(childs1);
                    if ( lastNode != null ){
                        if (s.contains(lastNode)){
                            bfsMap = null;
                            break;
                        }
                    }
                    bfsMap.put(counter, s);
                   
                }
                listNodes.addAll(childs1);//add ola stin linked list
                
                if (listNodes.size() > 1){
                    if ( this.stats.get(listNodes.getFirst()).level != this.stats.get(listNodes.get(1)).level ){//ean to epomeno stoixeio tis listas exei allo level tote auksise ton counter
                        counter ++;
                    }
                }
                else{//ean uparxei mono ena stoixeio stin lista auksise ton counter
                    counter ++;
                }
            }
            
            listNodes.removeFirst();//remove to prwto stoixeio tis linkedlist giati to exoume tsekarei
        }
      
   
        return bfsMap;
    
    }

    
    @Override
    public void computeWeights(Data dataset, String column) {
        for(Double node : this.stats.keySet()){
            NodeStats s = this.stats.get(node);
            s.weight = 0;
        }
        
        //find index of column
        Integer c;
        double[][] data = dataset.getData();
        for(c  = 0 ; c < dataset.getColumnsPosition().keySet().size() ; c++){
            if(dataset.getColumnsPosition().get(c).equals(column)){
                break;
            }
        }
        
        //System.out.println(c);
       
        for (double[] columnData : data) {
            NodeStats s = this.stats.get(columnData[c]);
            
            if(s != null){      //find weights of leaf level
                List<Double> cList = this.children.get(columnData[c]);
                if(cList == null || cList.isEmpty()){

                    //System.out.println(columnData[c]);
                    s.weight++;                         
                }
            }
        }

        //find weights for inner nodes
        for(int j = this.allParents.keySet().size()-1 ; j>=0 ; j--){
            for(Double node : this.allParents.get(j)){
                Integer totalWeight = 0;
                List<Double> cList = this.children.get(node);
                //System.out.println(node + " has children: " + cList);
                if(cList != null && !cList.isEmpty()){
                    for(Double child : cList){
                        totalWeight += this.stats.get(child).weight;
                    }
                    this.stats.get(node).weight = totalWeight;
                    //System.out.println(node + " weights " + totalWeight);
                }
                
            }
        }
    }
    
    
    @Override
    public Integer getWeight(Double node){
        //System.out.println("geteWeight " + node);
        return this.stats.get(node).weight;
    }
    
    
    public void incWeight(Double node){
        this.stats.get(node).weight++;
    }

    @Override
    public void setHierachyType(String type) {
        this.hierarchyType = type;
    }

    @Override
    public String getHierarchyType() {
        return this.hierarchyType;
    }
    
    @Override
    public void autogenerate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean validCheck(String parsePoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void buildDictionary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary getDictionary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transformParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Integer> getParentsInteger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLevelSize(int level) {
        return this.allParents.get(this.height - level - 1).size();
    }

    
 
}   
