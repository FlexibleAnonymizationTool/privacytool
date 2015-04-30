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
package privacytool.framework.algorithms.incognito;

/**
 * A node element
 * @author serafeim
 */
public class GeneralizationNodeElement {
    /**
     * The quasi identifier id
     */
    public int qid = -1;
    
    /**
     * The level of the QI's hierarchy tree described by this element
     */
    public int level = -1;
    
    /**
     * Constructor of the class
     * @param _qid a quasi identifier id 
     * @param _level the level of the QI's hierarchy tree
     */
    public GeneralizationNodeElement(int _qid, int _level){
        qid = _qid;
        level = _level;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        GeneralizationNodeElement e = (GeneralizationNodeElement) obj;
        return ((this.qid == e.qid) && (this.level == e.level));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.qid) ^ (Double.doubleToLongBits(this.qid) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.level) ^ (Double.doubleToLongBits(this.level) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return qid + "" + level; 
    }
}
