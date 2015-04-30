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

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author jimakos
 */
public class Sorting {
//    private LatticeNode[] nodes = null;
    private final int[] maxLevels;
    private final int[][] distinctValues;
    
    
    
    public Sorting (int[] maxLevels, int[][] distinctValues ){
//        this.nodes = nodes;
        this.maxLevels = maxLevels;
        this.distinctValues = distinctValues;
    }
    
    public LatticeNode[] sort(LatticeNode[] nodes){
        
        if ( nodes.length > 1){
            Arrays.sort(nodes, new LatticeNodeComparator());
        }
        
        return nodes;
        
    }
    
    class LatticeNodeComparator implements Comparator<LatticeNode>{

        @Override
        public int compare(LatticeNode n1, LatticeNode n2) {
//            System.out.println("C1: " + n1.toString() + " " + n2.toString() + " | " + n1.getLevel() + " " + n2.getLevel());
            //criterion c1
            if(n1.getLevel() < n2.getLevel()){
                return -1;
            }
            else if(n1.getLevel() > n2.getLevel()){
                return 1;
            }
            else{
//                System.out.println("C2: " + n1.toString() + " " + n2.toString() + " | " + n1.getAvgGeneralization(maxLevels) + " " + n2.getAvgGeneralization(maxLevels));
                //criterion c2
                if(n1.getAvgGeneralization(maxLevels) < n2.getAvgGeneralization(maxLevels)){
                    return -1;
                }
                else if(n1.getAvgGeneralization(maxLevels) > n2.getAvgGeneralization(maxLevels)){
                    return 1;
                }
                else{
//                    System.out.println("C3: " + n1.toString() + " " + n2.toString() + " | " + n1.getDistinctValuesAvgGen(distinctValues) + " " + n2.getDistinctValuesAvgGen(distinctValues));
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
