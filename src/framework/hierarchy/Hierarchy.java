/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import privacytool.framework.data.Data;
import privacytool.framework.dictionary.Dictionary;

/**
 * Interface of the hierarchy
 * @author jimakos
 * @param <T>
 */
public interface Hierarchy <T> {
    public int[][] getHierarchy();
    public void setHierarchy();
    public int getHierarchyLength();
    public void print();
    
    public void load();
    public List<T> getChildren(T parent);
    public Integer getLevel(T node);
    public String getNodesType();
    public T getParent(T node);
//    public List<T> getSiblings(T node);
    public T getRoot();
    public String getName();
    public Map<Integer, ArrayList<T>> getAllParents();
    public void export(String file);
    public void findAllParents();
    public boolean contains(T o);

    public Integer getHeight();
    public void setHierachyType(String type);
    public String getHierarchyType();
    
    public void add(T newObj, T parent);
    public void clear();
    public void edit(T oldValue, T newValue);
    public Map<Integer, Set<T>> remove(T obj);
    public Map<Integer,Set<T>> dragAndDrop(T firstObj,T lastObj);
    public Map<Integer,Set<T>> BFS(T firstnode,T lastNode);
    
    public void computeWeights(Data dataset, String column);
    public Integer getWeight(T node);
    public int getLevelSize(int level);
    
    public void autogenerate();
    
    public void buildDictionary();
    public Dictionary getDictionary();
    
    public boolean validCheck(String parsePoint);
    public void transformParents();
    public  Map<Integer,Integer> getParentsInteger();
    public T getParent(Double d);
}
