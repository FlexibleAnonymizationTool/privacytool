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
package privacytool.framework.hierarchy.distinct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import privacytool.framework.data.Data;
import privacytool.framework.dictionary.Dictionary;
import privacytool.framework.hierarchy.NodeStats;

/**
 *
 * @author serafeim
 */
public class AutoHierarchyImplString extends HierarchyImplString {
    //variables for autogenerating 
    String attribute = null;
    String sorting = null;
    int fanout = 0;
    int plusMinusFanout = 0;
    //boolean exact = false;  
    Data dataset = null;
    int randomNumber = 0;
    
    //generator for random numbers
    Random gen = new Random();
    
    public AutoHierarchyImplString(String _name, String _nodesType, String _hierarchyType, String _attribute, 
                                    String _sorting, int _fanout, int _plusMinusFanout, Data _data) {
        super(_name, _nodesType, _hierarchyType);
        attribute = _attribute;
        sorting = _sorting;
        fanout = _fanout;
        plusMinusFanout = _plusMinusFanout;
        dataset = _data;
    }
    
    @Override
    public void autogenerate() {
        int column = dataset.getColumnByName(attribute);
        double[][] data = dataset.getData();
        Dictionary dict = dataset.getDictionary(column);
        
        Set<String> itemsSet = new HashSet<>();
        long start = System.currentTimeMillis();
        long start1 = System.currentTimeMillis();                
        for (double[] columnData : data){
            //System.out.println(columnData[column]);
            itemsSet.add(dict.getIdToString((int)columnData[column]));
        }
       
        height = computeHeight(fanout, itemsSet.size());
        int curHeight = height - 1;
        System.out.println("size: " + itemsSet.size() + " fanout: " + fanout + " height: " + height);

        //build leaf level 
        ArrayList<String> initList = new ArrayList<>(itemsSet);

        
        if(sorting.equals("random")){
            Collections.shuffle(initList);
        } 
        else if(sorting.equals("alphabetical")){
            Collections.sort(initList);
        }

        long end1 = System.currentTimeMillis(); 
        System.out.println("Time to put to init list " + (end1 - start1));
//        System.out.println("put to " + curHeight + " " + initList.size());
        allParents.put(curHeight, initList);
 
        //build inner nodes of hierarchy
        while(curHeight > 0){
//            ArrayList<Double> prevLevel = allParents.get(curHeight);
            String[] prevLevel = allParents.get(curHeight).toArray(new String[allParents.get(curHeight).size()]);
            int prevLevelIndex = 0;
//            ArrayList<Double> curLevel = new ArrayList<>(); // resize to new size //arrays instead of array lists
            
            int curLevelSize = (int)(prevLevel.length / fanout + 1);
            if(fanout > 0){
                curLevelSize = prevLevel.length;
            }
            
//            System.out.println(prevLevel.length / fanout + "In level " + curHeight + " curLevelSize " + curLevelSize);
            
            String[] curLevel = new String[curLevelSize];
            int curLevelIndex = 0;
            
            while(prevLevelIndex < prevLevel.length){
                
                String ran = randomNumber();
                int curFanout = calculateRandomFanout();
//                List<Double> tempList = new ArrayList<>(); //oxi
                String[] tempArray = new String[curFanout];
                //templist new array(currFanout)
                
                //assign a parent every #curFanout children
                int j;
                for(j=0; j<curFanout && (prevLevelIndex < prevLevel.length); j++){
                    String ch = prevLevel[prevLevelIndex];
//                    System.out.println(prevLevelIndex);
                    prevLevelIndex++;
//                    tempList.add(ch);
                    tempArray[j] = ch;
                    parents.put(ch, ran);
                    stats.put(ch, new NodeStats(curHeight));
                }
                
                //array size is not curFanout (elements finished), resize 
                if(j != curFanout){
                    tempArray = Arrays.copyOf(tempArray, j);
                }
//                System.out.println(ran + " -> " + tempList);
                children.put(ran, new ArrayList<>(Arrays.asList(tempArray)));
//                assignSiblings(Arrays.asList(tempArray));
//                curLevel.add(ran);
                curLevel[curLevelIndex] = ran;
                curLevelIndex++;
            }

            curHeight--;

            //resize if there are less items in level than initial level max prediction
            if(curLevelIndex != curLevelSize){
//                System.out.println("level : " + curHeight + "index : " + curLevelIndex + " size " + curLevelSize);
                curLevel = Arrays.copyOf(curLevel, curLevelIndex);
            }

            allParents.put(curHeight, new ArrayList<>(Arrays.asList(curLevel)));
        }

//        System.out.println("put to " + curHeight);
        root = allParents.get(0).get(0);
        stats.put(root, new NodeStats(0));
        
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        
//        System.out.println("--------" + allParents.keySet().size() + " --------------------------------------------");
//        for(Integer i : allParents.keySet()){
//            System.out.println(i + " " + allParents.get(i));
//        }
//        System.out.println("----------------------------------------------------");
    }
    
    
    private int computeHeight(int fanout, int nodes){// fanout > 1
        int answer =  (int)(Math.log((double)nodes) / Math.log((double)fanout) + 1);
        if((Math.log((double)nodes) % Math.log((double)fanout)) != 0){
            answer++;
        }
        return answer;
    }
    
    private String randomNumber(){
        return "Random" + randomNumber++;
    }
    
//    private void assignSiblings(List<String> list){
//        for (String l : list){
//            List<String> sibs = new ArrayList<>(list);
//            sibs.remove(l);
//            siblings.put(l, sibs);
//        }
//    }
    
    private int calculateRandomFanout(){
        //return (gen.nextInt(fanout + plusMinusFanout) + fanout - plusMinusFanout);
        int Max = fanout + plusMinusFanout;
        int Min = fanout - plusMinusFanout;
        
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }
}
