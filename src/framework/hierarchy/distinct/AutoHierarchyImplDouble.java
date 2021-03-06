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
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.NodeStats;

/**
 * Class for autogenerating double hierarchies
 * @author serafeim
 */
public class AutoHierarchyImplDouble extends HierarchyImplDouble{
    
    //variables needed for autogenerating 
    String attribute = null;
    String sorting = null;
    int fanout = 0;
    int plusMinusFanout = 0;
    Data dataset = null;
    double randomNumber = 0;
    
    //generator for random numbers
    Random gen = new Random();
    
    /**
     * Class constructor
     * @param _name name of the hierarchy
     * @param _nodesType type of hierarchy's nodes
     * @param _hierarchyType type of hierarchy (distinct/range)
     * @param _attribute dataset attribute on which hierarchy will be autogenerated
     * @param _sorting type of sorting in the hierarchy
     * @param _fanout fanout to be used
     * @param _plusMinusFanout window of fanout
     * @param _data dataset loaded
     */
    public AutoHierarchyImplDouble(String _name, String _nodesType, String _hierarchyType, String _attribute, 
                                    String _sorting, int _fanout, int _plusMinusFanout, Data _data) {
        super(_name, _nodesType, _hierarchyType);
        attribute = _attribute;
        sorting = _sorting;
        fanout = _fanout;
        plusMinusFanout = _plusMinusFanout;
        dataset = _data;
    }
    
    /**
     * Automatically generates hierarchy's structures
     */
    @Override
    public void autogenerate() {
        int column = dataset.getColumnByName(attribute);
        Set<Double> itemsSet = new TreeSet<>();
        

//        long start = System.currentTimeMillis();
        
//        long start1 = System.currentTimeMillis();
        for (double[] columnData : dataset.getData()){
            itemsSet.add(columnData[column]);
        }
        
       
        height = computeHeight(fanout, itemsSet.size());
        int curHeight = height - 1;
//        System.out.println("size: " + itemsSet.size() + " fanout: " + fanout + " height: " + height);

        //build leaf level 
        ArrayList<Double> initList = new ArrayList<>(itemsSet);

        randomNumber = initList.get(initList.size()-1);
        
        //apply sorting
        if(sorting.equals("random")){
            Collections.shuffle(initList);
        }
        else if(sorting.equals("alphabetical")){
            Comparator comp = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            };
            Collections.sort(initList, comp);
        }

//        long end1 = System.currentTimeMillis(); 
//        System.out.println("Time to put to init list " + (end1 - start1));
        
        allParents.put(curHeight, initList);
 
        //build inner nodes of hierarchy
        while(curHeight > 0){
            
            Double[] prevLevel = allParents.get(curHeight).toArray(new Double[allParents.get(curHeight).size()]);
            int prevLevelIndex = 0;
            
            int curLevelSize = (int)(prevLevel.length / fanout + 1);
            if(fanout > 0){
                curLevelSize = prevLevel.length;
            }

            Double[] curLevel = new Double[curLevelSize];
            int curLevelIndex = 0;
            
            while(prevLevelIndex < prevLevel.length){
                
                Double ran = randomNumber();
                int curFanout = calculateRandomFanout();
                Double[] tempArray = new Double[curFanout];
                
                //assign a parent every #curFanout children
                int j;
                for(j=0; j<curFanout && (prevLevelIndex < prevLevel.length); j++){
                    Double ch = prevLevel[prevLevelIndex];
                    prevLevelIndex++;
                    tempArray[j] = ch;
                    parents.put(ch, ran);
                    stats.put(ch, new NodeStats(curHeight));
                }
                
                //array size is not curFanout (elements finished), resize 
                if(j != curFanout){
                    tempArray = Arrays.copyOf(tempArray, j);
                }

                children.put(ran, new ArrayList<>(Arrays.asList(tempArray)));

                curLevel[curLevelIndex] = ran;
                curLevelIndex++;
            }

            curHeight--;

            //resize if there are less items in level than initial level max prediction
            if(curLevelIndex != curLevelSize){
                curLevel = Arrays.copyOf(curLevel, curLevelIndex);
            }

            allParents.put(curHeight, new ArrayList<>(Arrays.asList(curLevel)));
        }

        //set root element
        root = allParents.get(0).get(0);
        stats.put(root, new NodeStats(0));
        
//        long end = System.currentTimeMillis();
//        System.out.println("Time: " + (end - start));
    }
    
    /**
     * Computes height of the autogenerated hierarchy
     * @param fanout fanout to be used
     * @param nodes total nodes of leaf level
     * @return height of the autogenerated hierarchy
     */
    private int computeHeight(int fanout, int nodes){
        int answer =  (int)(Math.log((double)nodes) / Math.log((double)fanout) + 1);
        if((Math.log((double)nodes) % Math.log((double)fanout)) != 0){
            answer++;
        }
        return answer;
    }
    
    /**
     * Generates a random double number
     * @return a random double number
     */
    private Double randomNumber(){
        return ++randomNumber;
    }
    
    /**
     * Calculates random fanout
     * @return fanout to be used
     */
    private int calculateRandomFanout(){
        int Max = fanout + plusMinusFanout;
        int Min = fanout - plusMinusFanout;
        
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }
}
