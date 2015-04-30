/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
