/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.framework.data;

import java.util.Map;
import privacytool.framework.dictionary.Dictionary;

/**
 * Interface of data
 * @author serafeim
 */
public interface Data {
    public double[][] getData();
    public void setData();
    public int getDataLenght();
    public void print();
    public void save();
    public void preprocessing();
    public void readDataset();
    public Map <Integer,String> getColumnsTypes();
    public Map <Integer,String> getColumnsPosition();
    public Map <Integer,Dictionary> getDictionary();

    public Dictionary getDictionary(Integer column);
    public void setDictionary(Integer column, Dictionary dict);
    public int getColumnByName(String column);
    public void replaceColumnDictionary(Integer column, Dictionary dict);
}
