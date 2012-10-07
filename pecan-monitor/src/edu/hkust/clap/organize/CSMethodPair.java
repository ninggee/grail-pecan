package edu.hkust.clap.organize;

import java.io.Serializable;

import soot.toolkits.scalar.Pair;

public class CSMethodPair implements Serializable{

    public CSMethodPair( CSMethod o1, CSMethod o2 ) { this.o1 = o1; this.o2 = o2; }
   
    public int hashCode() {
    	int o1hash= 0;int o2hash=0;
    	if(o1!=null) o1hash = o1.hashCode();    
    	else {
			throw new RuntimeException();
		}
    	
    	if(o2!=null) o2hash  =o2.hashCode();
    	else {
			throw new RuntimeException();
		}
    	
        return  o1hash + o2hash;
    }
    public boolean equals( Object other ) {
        if( other instanceof CSMethodPair) {
        	CSMethodPair p = (CSMethodPair) other;
            return o1.equals( p.o1 ) && o2.equals( p.o2 );
        } else return false;
    }
    public String toString() {
        return "CSMethodPair "+o1+","+o2;
    }
    public CSMethod getO1() { return o1; }
    public CSMethod getO2() { return o2; }

    protected CSMethod o1;
    protected CSMethod o2;


}
