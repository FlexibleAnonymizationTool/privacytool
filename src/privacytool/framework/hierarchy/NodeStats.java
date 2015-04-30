/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.hierarchy;

/**
 * Class keeping stats for hierarchy nodes
 * @author serafeim
 */
public class NodeStats {
    /**
     * level of the node
     */
    public int level;
    
    /**
     * weight of hierarchy node (number of times present in the dataset) 
     */
    public int weight; 

    public NodeStats(int l){
        level = l;
        weight = 0;
    }

    public void setWeight(int w){
        this.weight = w;
    }

    public void setLevel(int l){
        this.level = l;
    }

    public int getWeight(){
        return this.weight;
    }

    public int getLevel(){
        return this.level;
    }
    
}
