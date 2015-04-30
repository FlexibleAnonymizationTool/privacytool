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
package privacytool.framework.algorithms.flash;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author serafeim
 */
public class Heap {

    private final int[] maxLevels;
    private final int[][] distinctValues;
    PriorityQueue<LatticeNode> queue = new PriorityQueue<>(10, new LatticeNodeComparator());
    
    Heap(int[] maxLevels, int[][] distinctValues) {
        this.maxLevels = maxLevels;
        this.distinctValues = distinctValues;
    }
    
    public boolean isEmpty(){
        return this.queue.isEmpty();
    }

    public LatticeNode extractMin(){
        return this.queue.poll();
    }
    
    public void add(LatticeNode node){
        this.queue.add(node);
    }
    
    class LatticeNodeComparator implements Comparator<LatticeNode>{

        @Override
        public int compare(LatticeNode n1, LatticeNode n2) {
//            System.out.println(n1.toString() + " " + n2.toString() + " | " + n1.getLevel() + " " + n2.getLevel());
            //criterion c1
            if(n1.getLevel() < n2.getLevel()){
                return -1;
            }
            else if(n1.getLevel() > n2.getLevel()){
                return 1;
            }
            else{
//                System.out.println(n1.toString() + " " + n2.toString() + " | " + n1.getAvgGeneralization(maxLevels) + " " + n2.getAvgGeneralization(maxLevels));
                //criterion c2
                if(n1.getAvgGeneralization(maxLevels) < n2.getAvgGeneralization(maxLevels)){
                    return -1;
                }
                else if(n1.getAvgGeneralization(maxLevels) > n2.getAvgGeneralization(maxLevels)){
                    return 1;
                }
                else{
//                    System.out.println(n1.toString() + " " + n2.toString() + " | " + n1.getDistinctValuesAvgGen(distinctValues) + " " + n2.getDistinctValuesAvgGen(distinctValues));
                    //criterion c3
                    if(n1.getDistinctValuesAvgGen(distinctValues) < n2.getDistinctValuesAvgGen(distinctValues)){
                        return -1;
                    }
                    else if(n1.getDistinctValuesAvgGen(distinctValues) > n2.getDistinctValuesAvgGen(distinctValues)){
                        return 1;
                    }
                }
            }
            
            return 0;
        }
        
    }
}
