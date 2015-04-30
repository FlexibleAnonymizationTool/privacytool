/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A dictionary for a string attribute
 * @author serafeim
 */
public class Dictionary {
    private Map<Integer, String> idToString = null;
    private Map<String,Integer> stringToId = null;
    
    
    public Dictionary(){
        idToString = new HashMap<Integer,String>();
        stringToId = new HashMap<String,Integer>();
    }
    
    /**
     * Assign id to string
     * @param key key
     * @param value string value
     */
    public void putIdToString(Integer key, String value){
        idToString.put(key, value);
    }
    
    /**
     * Gets string for the specified key
     * @param key key
     * @return string associated with key
     */
    public String getIdToString(Integer key){
        return idToString.get(key);
    }
    
    /**
     * Assign string to id
     * @param key key 
     * @param value string value
     */
    public void putStringToId(String key, Integer value){
        stringToId.put(key, value);
    }
    
    /**
     * Gets id for the specified string
     * @param key id
     * @return string value
     */
    public Integer getStringToId(String key){
        return stringToId.get(key);
    }
    
    
    /**
     * if string is present in the dictionary
     * @param key the string value
     * @return true if present, false otherwise
     */
    public boolean containsStringToId(String key){
        return stringToId.containsKey(key);
    }
    
    /**
     * Checks if this dictionary is subset of another one
     * @param dict2 the other dictionary
     * @return returns null if this dictionary is subset of dict2, otherwise 
     * the first string that is not present in hierarchy's dictionary
     */
    public String isSubsetOf(Dictionary dict2){
        
        Set<String> dict2Keyset = dict2.getKeyset();
        for(String s : this.stringToId.keySet()){
            if(!dict2Keyset.contains(s)){
                return s;
            }
        }
        
        return null;
    }
    
    /**
     * Get the set of strings in the dictionary
     * @return the set of dictionary's strings 
     */
    public Set<String> getKeyset(){
        return this.stringToId.keySet();
    }
}
