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
