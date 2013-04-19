/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public class ArmCycle implements Cycle {    
    // some common Cycle objects
    public static ArmCycle S1 = new ArmCycle(0, 1, 0, 0);
    
    // cycles for non-sequential, sequential, internal and coprocessor
    private int nc, sc, ic, cc;
    // lazily intialized, cached hashCode (EJ)
    private volatile int hashCode;
    
    public ArmCycle() {
    }
    
    public ArmCycle(int nc, int sc, int ic, int cc) {
        this.nc = nc;
        this.sc = sc;
        this.ic = ic;
        this.cc = cc;
    }
    
    public ArmCycle(ArmCycle other) {
        nc = other.nc;
        sc = other.sc;
        ic = other.ic;
        cc = other.cc;
    }
    
    @Override
    public void add(Cycle other) {
        ArmCycle src = (ArmCycle) other;
        nc += src.nc;
        sc += src.sc;
        ic += src.ic;
        cc += src.cc;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ArmCycle)) return false;
        ArmCycle other = (ArmCycle) o;
        return (nc == other.nc) &&
               (sc == other.sc) &&
               (ic == other.ic) &&
               (cc == other.cc);
    }
    
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = nc;
            result = (result << 8) + sc;
            result = (result << 8) + ic;
            result = (result << 8) + cc;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "(" + nc + "," + sc + "," + ic + "," + cc + ")";
    }
}
