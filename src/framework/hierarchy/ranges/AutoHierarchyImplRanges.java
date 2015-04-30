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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import privacytool.framework.hierarchy.NodeStats;

/**
 * Class for autogenerating range hierarchies
 * @author serafeim
 */
public class AutoHierarchyImplRanges extends HierarchyImplRanges{
    
    //variables for autogenerating 
    Double start = null;
    Double end = null;
    Double step = null;
    int fanout = 0;
    int plusMinusFanout = 0;  
    
    //generator for random numbers
    Random gen = new Random();
    
    /**
     * Class constructor
     * @param _name name of the hierarchy
     * @param _nodesType type of hierarchy's nodes
     * @param _hierarchyType type of hierarchy (distinct/range)
     * @param _start start of ranges domain
     * @param _end end of ranges domain
     * @param _step length of each range
     * @param _fanout to be used
     * @param _plusMinusFanout window of fanout
     */
    public AutoHierarchyImplRanges(String _name, String _nodesType, String _hierarchyType,
                Double _start, Double _end, Double _step, int _fanout, int _plusMinusFanout) {
        super(_name, _nodesType, _hierarchyType);
        start = _start;
        end = _end;
        step = _step;
        fanout = _fanout;
        plusMinusFanout = _plusMinusFanout;
    }

    /**
     * Automatically generates hierarchy's structures
     */
    @Override
    public void autogenerate() {
        
        //split domain to ranges using BigDecimal for accuracy
        ArrayList<Range> initList = new ArrayList<>();
        BigDecimal bdStart = new BigDecimal(start.toString());
        BigDecimal bdEnd = new BigDecimal(start.toString());
        BigDecimal bdFixEnd = new BigDecimal(end.toString());
        BigDecimal bdStep = new BigDecimal(step.toString());

        //generate ranges of leaf level
        while(bdEnd.compareTo(bdFixEnd) < 0){            
            bdEnd = bdStart.add(bdStep);
            if(bdEnd.compareTo(bdFixEnd) > 0 || bdEnd.compareTo(bdFixEnd) == 0){
                bdEnd = bdFixEnd;
            }
            Range r = new Range();
            r.lowerBound = bdStart.doubleValue();
            r.upperBound = bdEnd.doubleValue();
            initList.add(r);
            bdStart = bdStart.add(bdStep);
        }
        
        height = computeHeight(fanout, initList.size());
        int curHeight = height - 1;
//        System.out.println("size: " + initList.size() + " fanout: " + fanout + " height: " + height);
        
        //set leaf level
        allParents.put(curHeight, initList);
 
        //build inner nodes of hierarchy
        while(curHeight > 0){
            
            Range[] prevLevel = allParents.get(curHeight).toArray(new Range[allParents.get(curHeight).size()]);
            int prevLevelIndex = 0;
            
            int curLevelSize = (int)(prevLevel.length / fanout + 1);
            if(fanout > 0){
                curLevelSize = prevLevel.length;
            }

            Range[] curLevel = new Range[curLevelSize];
            int curLevelIndex = 0;
            
            while(prevLevelIndex < prevLevel.length){
                
                int curFanout = calculateRandomFanout();
                
                Range ran = new Range();
                Range[] tempArray = new Range[curFanout];            
                Range firstChild = null;
                Range lastChild = null;
                
                //check if is one child then move it one level up
                if(prevLevel.length - prevLevelIndex == 1){
                    ran = prevLevel[prevLevelIndex];
                    allParents.get(curHeight).remove(ran);
                    prevLevelIndex++;
                }
                else{

                    //assign a parent every #curFanout children
                    int j;
                    for(j=0; j<curFanout && (prevLevelIndex < prevLevel.length); j++){

                        Range ch = prevLevel[prevLevelIndex];
                        prevLevelIndex++;
                        tempArray[j] = ch;
                        parents.put(ch, ran);
                        stats.put(ch, new NodeStats(curHeight));
                        if(j == 0){
                            firstChild = ch;
//                            lastChild = ch;
                        }
                        else {
                            lastChild = ch;
                        }
                    }

                    ran.lowerBound = firstChild.lowerBound;
                    ran.upperBound = lastChild.upperBound;

                    //array is not curFanout (elements finished), resize 
                    if(j != curFanout){
                        tempArray = Arrays.copyOf(tempArray, j);
                    }

                    children.put(ran, new ArrayList<>(Arrays.asList(tempArray)));
                }
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

    }

    /**
     * Computes height of the autogenerated hierarchy
     * @param fanout fanout to be used
     * @param nodes total nodes of leaf level
     * @return height of the autogenerated hierarchy
     */
    private int computeHeight(int fanout, int nodes){// fanout > 1
        int answer =  (int)(Math.log((double)nodes) / Math.log((double)fanout) + 1);
        if((Math.log((double)nodes) % Math.log((double)fanout)) != 0){
            answer++;
        }
        return answer;
    }
    
//    private void assignSiblings(List<Range> list){
//        for (Range l : list){
//            List<Range> sibs = new ArrayList<>(list);
//            sibs.remove(l);
//            siblings.put(l, sibs);
//        }
//    }
    
    /**
     * Calculates random fanout
     * @return fanout to be used
     */
    private int calculateRandomFanout(){
        //return (gen.nextInt(fanout + plusMinusFanout) + fanout - plusMinusFanout);
        int Max = fanout + plusMinusFanout;
        int Min = fanout - plusMinusFanout;
        
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }
}
