/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public class CpuCycle {
    private int codeN, codeS, dataN, dataS, coreI;
    
    public static final CpuCycle CODE_S1;
    
    static {
        CODE_S1 = new CpuCycle(0, 1, 0, 0, 0);
    }
    
    public CpuCycle(int codeN, int codeS, int dataN, int dataS, int coreI) {
        this.codeN = codeN;
        this.codeS = codeS;
        this.dataN = dataN;
        this.dataS = dataS;
        this.coreI = coreI;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CpuCycle))
            return false;
        CpuCycle cc = (CpuCycle) o;
        // each member will not exceed 2^6 (64), thus it is fine to use hashCode
        // to determine equality (see implmentation of hashCode for details)
        return hashCode() == o.hashCode();
    }
    
    @Override
    public int hashCode() {
        return codeN << 26 | codeS << 20 | dataN << 14 | dataS << 8 | coreI;
    }
    
    public int toClockCycle() {
        int result = coreI;
        result += codeN * WaitState.CODE_N.get();
        result += codeS * WaitState.CODE_S.get();
        result += dataN * WaitState.DATA_N.get();
        result += dataS * WaitState.DATA_S.get();
        return result;
    }
}
