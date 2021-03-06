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
package privacytool.framework.hierarchy.ranges;

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
import privacytool.framework.hierarchy.distinct.HierarchyImplDouble;

/**
 *
 * @author serafeim
 */
public class HierarchyImplRanges implements Hierarchy<Range>{

    String inputFile = null;
    String name = null;
    String nodesType = null;
    String hierarchyType = null;
    int height = -1;
    BufferedReader br = null;
    Range root = null;
    
    Map<Range, List<Range>> children = new HashMap<>();
    Map<Range, NodeStats> stats = new HashMap<>();
    Map<Range, Range> parents = new HashMap<>();
//    Map<Range, List<Range>> siblings = new HashMap<>();
    Map<Integer,ArrayList<Range>> allParents = new HashMap<>();
    
    public HierarchyImplRanges(String inputFile){
        this.inputFile = inputFile;
    }
    
    public HierarchyImplRanges(String _name, String _nodesType, String _hierarchyType){
        this.name = _name;
        this.nodesType = _nodesType;
        this.hierarchyType = _hierarchyType;
    }

    @Override
    public void load() {
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
    
    private void loadHierarchy() throws IOException{
        String line;
        int curLevel = this.height - 1;
        
        while ((line = br.readLine()) != null) {
            String tokens[] = line.split(" ");
            if(line.trim().isEmpty()){
                curLevel--;
                continue;
            }
            
            
            //split parent
            Range pRange = new Range();
            String bounds[] = tokens[0].split(",");
            pRange.lowerBound = Double.parseDouble(bounds[0]);
            pRange.upperBound = Double.parseDouble(bounds[1]);
                    
            boolean isChild = false;
            List<Range> ch = new ArrayList<>();
            for (String token : tokens){
                if(token.equals("has")){ 
                    isChild = true;
                    continue;
                }
                Range newRange = new Range();
                bounds = token.split(",");
                newRange.lowerBound = Double.parseDouble(bounds[0]);
                newRange.upperBound = Double.parseDouble(bounds[1]);
                
                if(isChild){
                    
                    ch.add(newRange);             
                    this.stats.put(newRange, new NodeStats(curLevel));
                    
                    
                    this.parents.put(newRange, pRange);  
                }
                else{
                    this.stats.put(newRange, new NodeStats(curLevel-1));
                    
                    if(curLevel - 1 == 0){
                        root = pRange;
                    }
                }
            }
            this.children.put(pRange, ch);
            
//            for (Range child : ch) {
//                List<Range> sib = new ArrayList<>(ch);
//                sib.remove(child);
//                this.siblings.put(child, sib);
//            }
            
        }
        int mb = 1024*1024;
        System.out.println((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        System.out.println(Runtime.getRuntime().totalMemory()/mb);
        System.out.println(Runtime.getRuntime().totalMemory());
    }
        
    @Override
    public List<Range> getChildren(Range parent) {
        return this.children.get(parent);
    }

    @Override
    public Integer getLevel(Range node) {
        return this.stats.get(node).level;
    }

    @Override
    public Range getParent(Range node) {
        return this.parents.get(node);
    }

//    @Override
//    public List<Range> getSiblings(Range node) {
//        return this.siblings.get(node);
//    }
    
    @Override
    public int[][] getHierarchy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHierarchy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHierarchyLength() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range getRoot() {
        return this.allParents.get(0).get(0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<Integer, ArrayList<Range>> getAllParents() {
        return this.allParents;
    }
    
    public void findAllParents(){
        List<Range> tempChild = null;
        int i = 0;
        int level = 0;
        ArrayList<Range> tempArr1 = new ArrayList<>();
        ArrayList<Range> tempArr2 = new ArrayList<>();
        
        tempArr1.add(root);
        allParents.put(level, tempArr1);
        tempArr2 = (ArrayList<Range>) tempArr1.clone();
        level ++;
        
        while (level < height - 1 ){
            tempArr1 = new ArrayList<>();
            for (Range x : tempArr2) {
                tempChild = children.get(x);
                if ( tempChild != null){
                    for ( i = 0 ; i < tempChild.size() ; i ++ ){
                        tempArr1.add(tempChild.get(i));
                    }
                }
            }           
            allParents.put(level, tempArr1);
            tempArr2 = (ArrayList<Range>) tempArr1.clone();
            level ++;  
        }
    }
    
    @Override
    public void export(String file) {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("range");
            writer.println("column " + this.name);
            writer.println("type " + this.nodesType);
            writer.println("height " + this.height);
            writer.println();

            //write parents - childen to file
            for(int curLevel = height - 2; curLevel >= 0; curLevel--){
                //System.out.println("i = " + curLevel + "\t children = " + this.allParents.get(curLevel).toString() );
                for (Range curParent : this.allParents.get(curLevel)){
                    StringBuilder sb = new StringBuilder();
                    System.out.println("cur parent = " + curParent);
                    if (this.getChildren(curParent) != null){
                        for (Range child : this.getChildren(curParent)){
                            sb.append(((Range)child).lowerBound);
                            sb.append(",");
                            sb.append(((Range)child).upperBound);
                            sb.append(" ");
                        } 
                        writer.println(((Range)curParent).lowerBound + "," + ((Range)curParent).upperBound + " has " + sb.toString());
                    }
                    
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
        return this.nodesType;
    }

    @Override
    public void add(Range newObj, Range parent) {
        System.out.println("add () newItem: "  + newObj.toString() + " parentItem: " + parent.toString());
       
        
        if(parent != null){
            this.stats.put(newObj, new NodeStats(this.stats.get(parent).getLevel()+1));
            
            
           /* System.out.println("before");
            for (Map.Entry<Double, Double> entry : parents.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }*/
            
            this.parents.put(newObj, parent);
            
            /*System.out.println("after");
            for (Map.Entry<Double, Double> entry : parents.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }*/
            
            
            //add siblings
//            List<Double> cList = this.children.get(parent);
//            List<Double> sibs = new ArrayList<>();
//            if(cList != null)
//                sibs.addAll(cList);
//            this.siblings.put(newObj, sibs);
//            for(Double sib : sibs){
//                this.siblings.get(sib).add(newObj);
//            }
            
            
            /*System.out.println("before");
            for (Map.Entry<Double, List<Double>> entry : children.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }*/
            
            this.children.put(newObj, new ArrayList<Range>());
            if(this.children.get(parent) != null){
                this.children.get(parent).add(newObj);
            }else{
                ArrayList<Range> c = new ArrayList<>();
                c.add(newObj);
                this.children.put(parent, c);
            }
            
            /*System.out.println("after");
            for (Map.Entry<Double, List<Double>> entry : children.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }*/
            
            
            //System.out.println(height + " " + this.levels.get(parent));
            
            
            //parent is a leaf node
            
           
            if (this.stats.get(parent).getLevel() == allParents.size() - 1){
                ArrayList<Range> p = new ArrayList<>();
                p.add(newObj);
                this.allParents.put(this.stats.get(parent).getLevel() + 1, p);
                this.height++;
                
            }    
            else{
                ArrayList<Range> childList = allParents.get(this.stats.get(parent).getLevel() + 1);
                childList.add(newObj);
                allParents.put(this.stats.get(parent).getLevel() + 1, childList);
                
            }
            
            /*System.out.println("after");
            for (Map.Entry<Integer, ArrayList<Double>> entry : allParents.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }*/
            
        }
        else{
            System.out.println("Error: parent is null!");
        }    
    }
    
    private boolean isLeafLevel(ArrayList<Range> parentsInLevel){
        boolean  leafLevel = true;
        
        for (Range parent : parentsInLevel){
            List<Range> ch = this.children.get(parent);
            if(ch != null && ch.size() > 0){
                leafLevel = true;
                break;
            }
        }
        return leafLevel;
    }
    
    @Override
    public void edit(Range oldValue, Range newValue){
        //update children map 
        //update children map 
        Range parent = null;
        ArrayList parentsList = null;
        List<Range> childrenListNew = null;
        
        System.out.println(this.stats.get(oldValue).getLevel());
        System.out.println("before");
            for (Map.Entry<Range, List<Range>> entry : this.children.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        
        List<Range> childrenList = this.children.get(oldValue);
        if(childrenList != null){//node
            if ( allParents.get(0).get(0).equals(oldValue)){
                System.out.println("root");
                
                //children
                this.children.put(newValue, childrenList);
                this.children.remove(oldValue);
                
                //parents
                for ( int i = 0; i < childrenList.size() ; i ++ ){
                    this.parents.remove(childrenList.get(i));
                    this.parents.put(childrenList.get(i), newValue);
                }
                
                //allParents
                 parentsList = allParents.get(0);
                 parentsList.remove(0);
                 parentsList.add(newValue);
                 allParents.put(0,parentsList);
            }
            else{
                System.out.println("node");
                
                //children structure
                //its children
                this.children.put(newValue, childrenList);
                this.children.remove(oldValue); 
                
                //his father children
                parent = this.parents.get(oldValue);
                childrenListNew = this.children.get(parent);
                for ( int i = 0 ; i < childrenListNew.size() ; i ++ ){
                    if ( childrenListNew.get(i).equals(oldValue)){
                        childrenListNew.remove(i);
                        childrenListNew.add(newValue);
                        this.children.put(parent, childrenListNew);
                        break;
                    }
                }
                
                //parent structure
                //its parent   
                this.parents.put(newValue, parent);
                this.parents.remove(oldValue);
                
                //its children's father 
                for ( int i = 0; i < childrenList.size() ; i ++ ){
                    this.parents.remove(childrenList.get(i));
                    this.parents.put(childrenList.get(i), newValue);
                }

                //allParents
                parentsList = allParents.get(this.stats.get(oldValue).getLevel());
                for ( int i = 0 ; i < parentsList.size() ; i ++ ) {
                    if (parentsList.get(i).equals(oldValue)){
                        parentsList.remove(i);
                        parentsList.add(newValue);
                        allParents.put(this.stats.get(oldValue).getLevel(), parentsList);
                        break;
                    }
                }

            }
            
        }
        else{//leaf
            
            //children
            parent = this.getParent(oldValue);
            childrenListNew = this.children.get(parent);
            for ( int i = 0 ; i < childrenListNew.size() ; i++ ){
                if ( childrenListNew.get(i).equals(oldValue)){  
                    childrenListNew.remove(i);
                    childrenListNew.add(newValue);
                    this.children.put(parent, childrenListNew);
                    break;
                }
            }
            
            //parents
            this.parents.put(newValue,parent);
            this.parents.remove(oldValue);
            
            
            //allParents
            parentsList = allParents.get(allParents.size()-1);
            for( int i = 0 ; i < parentsList.size() ; i ++ ){
                if (parentsList.get(i).equals(oldValue)){
                    parentsList.remove(i);
                    parentsList.add(newValue);
                    allParents.put(allParents.size()-1, parentsList);
                    break;
                }
            }
            
        }
        
        this.stats.put(newValue, this.stats.get(oldValue));
        this.stats.remove(oldValue);
        
        System.out.println("after");
            for (Map.Entry<Range, List<Range>> entry : this.children.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
            
        System.out.println("after");
            for (Map.Entry<Range, Range> entry : this.parents.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("after");
            for (Map.Entry<Integer, ArrayList<Range>> entry : this.allParents.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println("after");
            for (Map.Entry<Range, NodeStats> entry : this.stats.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
            
        /*
        if(parent != null){
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
        */        
    }

    @Override
    public boolean contains(Range o){  
        return stats.get(o) != null;
    }

    @Override
    public Map<Integer, Set<Range>> remove(Range item) {
        Map<Integer, Set<Range>> nodesMap = BFS(item,null);
        for(Integer i = nodesMap.keySet().size() ; i > 0 ; i--)
        {
            //System.out.println(i + "-> " + nodesMap.get((i)));
            
            for(Range itemToDelete : nodesMap.get(i)){
                //System.out.println(itemToDelete.toString());
                if(itemToDelete.equals(root)){
                    System.out.println("Cannot remove root");
                    return null;
                }
                children.remove(itemToDelete);
                children.get(getParent(itemToDelete)).remove(itemToDelete);
                parents.remove(itemToDelete);
//                List<Range> sibs = siblings.get(itemToDelete);
//                if(sibs != null){
//                    for(Range sib : sibs){
//                        if(siblings.get(sib) != null)
//                            siblings.get(sib).remove(itemToDelete);
//                    }
//                }
//                siblings.remove(itemToDelete);
                List<Range> p = allParents.get(stats.get(itemToDelete).level);
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
        children.put(root, new ArrayList<Range>());
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
    public Map<Integer,Set<Range>> dragAndDrop(Range firstObj, Range lastObj) {
        Range parentFirstObj ;
        ArrayList childs1 = null;
        ArrayList childs2 = null;
        ArrayList simb1 = null;
        ArrayList simb2 = null;
        ArrayList parents = null;
        int levelFirstObj;
        int levelLastObj;
        int levelObj;
        int newLevel;
        Set<Range> s = null;
        NodeStats nodeStat = null;
        
        
        
        System.out.println("Drag and Dropppppppp");
        
        System.out.println("before");
        for (Map.Entry<Range, NodeStats> entry : this.stats.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue().level +":"+entry.getValue().weight);
        }   
        
        /*System.out.println("before");
            for (Map.Entry<Double, List<Double>> entry : this.children.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
                List <Double> d = entry.getValue();
                for( int i = 0 ; i < d.size() ; i ++  ){
                    System.out.println(d.get(i) + "level:" + this.getLevel(d.get(i)) );
                }
        }*/
            
        System.out.println("old height = " + this.height);
        
        Map<Integer,Set<Range>> m = this.BFS(firstObj,lastObj);
        
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
                this.children.put(lastObj, childs1);
            }


            //diagrafw to allparents tou prwtou kai tou dentrou tou
            for ( int i = 1 ; i <= m.size() ; i ++ ){
                s = m.get(i);
                for( Range node : s ){
                    System.out.println("node = " + node );
                    levelObj = this.getLevel(node);
                    parents = this.allParents.get(levelObj);
                    System.out.println("level = " + levelObj);
                    for( int j = 0 ; j < parents.size() ; j++ ){
                        if ( parents.get(j).equals(node)){
                            parents.remove(j);
                        }
                    }
                }
            }
            
            //topothetw ton prwto komvo kai to dentro tou sto allparents
            levelLastObj = this.getLevel(lastObj);
            
            //simb2 = this.allParents.get(levelLastObj);
            //simb2.add(lastObj);
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
                for (Range d : s){
                    nodeStat = this.stats.get(d);
                    nodeStat.level = newLevel;
                }
            }
            
            //height
            this.height = allParents.size();
            System.out.println("new height = " + this.height);
            
        }
        
        System.out.println("after");
        for (Map.Entry<Range, NodeStats> entry : this.stats.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue().level +":"+entry.getValue().weight);
        }  
        
        
        /*System.out.println("after");
        for (Map.Entry<Double, List<Double>> entry : this.children.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue().toString());
            List <Double> d = entry.getValue();
                for( int i = 0 ; i < d.size() ; i ++  ){
                    System.out.println(d.get(i) + "level:" + this.getLevel(d.get(i)) );
                }
        }*/
        
        return m;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public void computeWeights(Data dataset, String column) {
        
        //all weights set to zero
        for(Range node : this.stats.keySet()){
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
        
//        System.out.println(c);
        
        //assign values to nodes, starting from root
        
        for (double[] columnData : data) {
            compute(getRoot(), columnData[c]);
        }               
    }
    
    public void compute(Range r, Double value){
        
        //range has been deleted
//        if(this.stats.get(r) == null){
//            System.out.println(r.toString() + " not found");
//            return;
//        }
        
        if(r.contains(value)){
//            System.out.println(r.toString());
            this.stats.get(r).weight++;
        }
        
        List<Range> ch = this.children.get(r);
        if(ch != null){
            for(Range c : ch){
                if(c.contains(value))
                    compute(c, value);
            }
        }
    }
  
    @Override
    public Integer getWeight(Range node){
        return this.stats.get(node).weight;
    }
    
    public void incWeight(Range node){
        this.stats.get(node).weight++;
    }

    @Override
    public Map<Integer, Set<Range>> BFS(Range firstnode, Range lastNode) {
        Map<Integer,Set<Range>> bfsMap = new HashMap<Integer,Set<Range>>();
        LinkedList<Range> listNodes = new LinkedList<Range>();
        ArrayList childs1 = null;
        int counter = 1;
        int levelNode1;
        int levelNode2;
        Set s = new HashSet<Range>();
        
        
        s.add(firstnode);
        bfsMap.put(counter,s);
        listNodes.add(firstnode);
        counter ++;
        
        levelNode1 = this.getLevel(firstnode);
        
        while (!listNodes.isEmpty()){
            childs1 = (ArrayList) this.getChildren(listNodes.getFirst());
            if ( childs1 != null && childs1.size() > 0){// ean exei paidia
                levelNode2 = this.getLevel((Range) childs1.get(0));
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
                    s = new HashSet<Range>();
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
    
    public boolean validCheck(String nodeStr){
        boolean valid = true;
        Range node = new Range();
        String[] parts = nodeStr.split("-");
        if(parts.length == 2){
            node.lowerBound = Double.parseDouble(parts[0]);
            node.upperBound = Double.parseDouble(parts[1]);
        }
        
        List<Range> chList = children.get(node);
        if(chList != null){
            for(Range c : chList){
                valid = !node.overlays(c);
            }
        }
        return valid;
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
    public Range getParent(Double d) {
        
        List<Range> leafNodes = this.allParents.get(this.height-1);
        Range r = binarySearch(leafNodes, d);
//        System.out.println("Find parent of " + d + " is " + r.toString());
        return r;
    }

    private Range binarySearch(List<Range> list, Double d){
//        System.out.println("binary Search...");
        
        if(list.isEmpty()){
            return null;
        }
        
        int mid = list.size()/2;
        
//        System.out.println("mid = " + mid);
        if(list.size()%2 > 0){
            mid++;
        }
//        System.out.println("Mid of " + list.size() + " is " + mid);
        if(list.size() == 1){
            return list.get(0);
        }
        Range r = list.get(mid);
        
        
        if(d < r.lowerBound){
            //return binarySearch(list.subList(0, mid-1), d);
            for ( int i = 0 ; i < mid ; i ++ ){
                r = list.get(i);
                if ( r.contains(d)){
                    return r;
                }
            }
        }
        else if (d > r.upperBound){
            for ( int i = mid ; i < list.size() ; i ++ ){
                r = list.get(i);
                if ( r.contains(d)){
                    return r;
                }
            }
           //return binarySearch(list.subList(mid+1, list.size()-1), d);
        }
        else{
            return r;
        }
        
        /*if(d < r.lowerBound){
            return binarySearch(list.subList(0, mid-1), d);
        }
        else if (d > r.upperBound){
            
           return binarySearch(list.subList(mid+1, list.size()-1), d);
        }
        else{
            return r;
        }*/
        
        return null;
    }

    @Override
    public int getLevelSize(int level) {
        return this.allParents.get(this.height - level - 1).size();
    }
}
