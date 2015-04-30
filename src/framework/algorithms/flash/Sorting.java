/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
