/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
