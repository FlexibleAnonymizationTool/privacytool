/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package privacytool.framework.algorithms.flash;


import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 *
 * @author serafeim
 */
public class HistoryBuffers{
    
    private static final float   hashTableLoadFactor = 0.75f;
    
    private LinkedHashMap<LatticeNode, Buffer> map = null;
    private int cacheSize = 0;
    
    /**
     * Creates a new LRU cache.
     * @param cacheSize the maximum number of entries that will be kept in this cache.
     */
    public HistoryBuffers (int cacheSize) {
        this.cacheSize = cacheSize;
        int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;
        
        map = new LinkedHashMap<LatticeNode, Buffer>(hashTableCapacity, hashTableLoadFactor, true) {
            private static final long serialVersionUID = 1;
            @Override
            protected boolean removeEldestEntry (Map.Entry<LatticeNode, Buffer> eldest) {
                return size() > HistoryBuffers.this.cacheSize;
            }
        };
    }
    
    /**
     * Retrieves an entry from the cache.<br>
     * The retrieved entry becomes the MRU (most recently used) entry.
     * @param key the key whose associated value is to be returned.
     * @return    the value associated to this key, or null if no value with this key exists in the cache.
     */
    public synchronized Buffer get (LatticeNode key) {
        return map.get(key);
    }
    
    public int getSizeOfMap(){
        return map.size();
    }
    
    /**
     * Adds an entry to this cache.
     * The new entry becomes the MRU (most recently used) entry.
     * If an entry with the specified key already exists in the cache, it is replaced by the new entry.
     * If the cache is full, the LRU (least recently used) entry is removed from the cache.
     * @param key    the key with which the specified value is to be associated.
     * @param value  a value to be associated with the specified key.
     */
    public synchronized void put (LatticeNode key, Buffer value) {
        map.put (key, value);
    }
    
    /**
     * Clears the cache.
     */
    public synchronized void clear() {
        map.clear();
    }
    
    /**
     * Returns the number of used entries in the cache.
     * @return the number of entries currently in the cache.
     */
    public synchronized int usedEntries() {
        return map.size();
    }
    
    /**
     * Returns a <code>Collection</code> that contains a copy of all cache entries.
     * @return a <code>Collection</code> with a copy of the cache content.
     */
    public synchronized Collection<Map.Entry<LatticeNode, Buffer>> getAll() {
        return new ArrayList<>(map.entrySet());
    }
    
//    public LatticeNode findBestNode(LatticeNode node){
//        
//        LatticeNode bestNode = null;
//        int bestDiff = 0;
//        int nodeLenght = node.getTransformation().length;
//        int[] nodeTransf = node.getTransformation();
//        
//        for(LatticeNode hnode : this.map.keySet()){
//            
//            int[] hnodeTrasnf = hnode.getTransformation();
//            boolean isValid = true;
//            int curDiff = 0;
//            
//            for(int i=0; i<nodeLenght; i++){
//                if(hnodeTrasnf[i] > nodeTransf[i]){
//                    isValid = false;
//                    break;
//                }
//                else{
//                    curDiff += hnodeTrasnf[i] - nodeTransf[i];
//                }
//            }
//            
//            if(isValid){
//                if((bestNode == null) || (curDiff > bestDiff)){
//                    bestNode = hnode;
//                    bestDiff = curDiff;
//                }
//            }
//            
//        }
//        
//        return bestNode;
//    }
    
public LatticeNode findClosestNode(LatticeNode node){
        LatticeNode closestNode = null;
        int bestSize = 0;
        int nodeLenght = node.getTransformation().length;
        int[] nodeTransf = node.getTransformation();
        

        for(Entry<LatticeNode, Buffer> entry : this.map.entrySet()){
            
            LatticeNode hnode = entry.getKey();
            int[] hnodeTrasnf = hnode.getTransformation();
            boolean isValid = true;
            
            for(int i=0; i<nodeLenght; i++){
                if(hnodeTrasnf[i] > nodeTransf[i]){
                    isValid = false;
                    break;
                }
            }
            
            if(isValid){
                if((closestNode == null) || (entry.getValue().getSize() < bestSize)){
                    closestNode = hnode;
                    bestSize = entry.getValue().getSize();
                }
            }
            
        }
        
        return closestNode;
}
    
} // end class LRUCache