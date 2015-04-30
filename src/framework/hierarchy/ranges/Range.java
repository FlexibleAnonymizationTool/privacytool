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
package privacytool.framework.hierarchy.ranges;

/**
 *
 * @author serafeim
 */
public class Range {
    public Double lowerBound; 
    public Double upperBound;
    
    public Range(){
        
    }
    
    public Range(Double _lowerBound, Double _upperBound){
        this.lowerBound = _lowerBound;
        this.upperBound = _upperBound;
    }
    
    public void print(){
        System.out.println(lowerBound + ", " + upperBound);
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        Range r = (Range) obj;
        return (this.upperBound.equals(r.upperBound)) && (this.lowerBound.equals(r.lowerBound));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.lowerBound) ^ (Double.doubleToLongBits(this.lowerBound) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.upperBound) ^ (Double.doubleToLongBits(this.upperBound) >>> 32));
        return hash;
    }
    
     @Override 
     public String toString(){
         StringBuilder sb = new StringBuilder();
         sb.append(this.lowerBound);
         sb.append(" - ");
         sb.append(this.upperBound);
         return sb.toString();
     }

    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public Double getUpperBound() {
        return upperBound;
    }
    
    public boolean contains(Double v){
        return v >= this.lowerBound && v <= this.upperBound;
    }
     
    public boolean overlays(Range r){
        return (r.lowerBound < this.lowerBound && r.upperBound < this.lowerBound)
                || (r.lowerBound >= this.upperBound && r.upperBound >= this.upperBound);
    }
    
    public static Range parseRange(String str){
        String[] arr = str.split("-");
        double lowBound = Double.parseDouble(arr[0].trim());
        double highBound = Double.parseDouble(arr[1].trim());
        
        return new Range(lowBound, highBound);
    }
}
