/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public class Arm7Register implements Register {
    public final static int RegisterSize = 16;
    public final static int CpuModeTotal = 7;
    public final static int PC = 15;
    public final static int SignedMask = 1 << 31;
    public final static int ZeroMask = 1 << 30;
    public final static int CarryMask = 1 << 29;
    public final static int OverflowMask = 1 << 28;
    public final static int IrqMask = 1 << 7;
    public final static int FiqMask = 1 << 6;
    public final static int StateMask = 1 << 5;
    public final static int ModeMask = 0x1F;
    // ModeBits depends on the ordinal values of CpuMode
    public final static int[] ModeBits = {
        0x1F /* System */, 0x10 /* User */, 0x11 /* FIQ */, 0x13 /* Supervisor */,
        0x17 /* Abort */, 0x12 /* IRQ */, 0x1B /* Undefined */
    };
    
    private CpuMode cpuMode;
    private int[] R = new int[Arm7Register.RegisterSize];
    private int[][] regSet;
    private int cpsr;
    private int[] spsrSet = new int[Arm7Register.RegisterSize];
    private CpuState cpuState;
    
    
    public Arm7Register() {
        cpuMode = CpuMode.Supervisor;
        cpuState = CpuState.Arm;
        regSet = new int[CpuModeTotal][];
        for (int i = 0; i < regSet.length; ++i) {
            regSet[i] = new int[Arm7Register.RegisterSize];
        }
        // FIQ and IRQ are disabled, in ARM state, in Supervisor mode
        cpsr = (0b110 << 5) | 0b10011;
    }
    
    @Override
    public int get(int which) {
        return R[which];
    }
    
    @Override
    public void set(int which, int value) {
        R[which] = value;
    }
    
    @Override
    public int getCpsr() {
        return cpsr;
    }
    
    @Override
    public int getSpsr() {
        int modeIdx = cpuMode.ordinal();
        return spsrSet[modeIdx];
    }
    
    @Override
    public CpuState getCpuState() {
        return cpuState;
    }
    
    @Override
    public boolean isSigned() {
        return (cpsr & SignedMask) != 0;
    }
    
    @Override
    public void setSigned() {
        cpsr |= SignedMask;
    }
    
    @Override
    public void clearSigned() {
        cpsr &= ~SignedMask;
    }
    
    @Override
    public void toggleSigned() {
        if (isSigned()) clearSigned();
        else setSigned();
    }
    
    @Override
    public boolean isZero() {
        return (cpsr & ZeroMask) != 0;
    }
    
    @Override
    public void setZero() {
        cpsr |= ZeroMask;
    }
    
    @Override
    public void clearZero() {
        cpsr &= ~ZeroMask;
    }
    
    @Override
    public void toggleZero() {
        if (isZero()) clearZero();
        else setZero();
    }
    
    @Override
    public boolean isCarry() {
        return (cpsr & CarryMask) != 0;
    }
    
    @Override
    public void setCarry() {
        cpsr |= CarryMask;
    }
    
    @Override
    public void clearCarry() {
        cpsr &= ~CarryMask;
    }
    
    @Override
    public void toggleCarry() {
        if (isCarry()) clearCarry();
        else setCarry();
    }
    
    @Override
    public boolean isOverflow() {
        return (cpsr & OverflowMask) == 0;
    }
    
    @Override
    public void setOverflow() {
        cpsr |= OverflowMask;
    }
    
    @Override
    public void clearOverflow() {
        cpsr &= ~OverflowMask;
    }
    
    @Override
    public void toggleOverflow() {
        if (isOverflow()) clearOverflow();
        else setOverflow();
    }
    
    @Override
    public boolean isIrq() {
        return (cpsr & ~IrqMask) == 0;
    }

    @Override
    public void enableIrq() {
        cpsr &= ~IrqMask;
    }
    
    @Override
    public void disableIrq() {
        cpsr |= IrqMask;
    }
    
    @Override
    public boolean isFiq() {
        return (cpsr & ~FiqMask) == 0;
    }   
    
    @Override
    public void enableFiq() {
        cpsr &= ~FiqMask;
    }
    
    @Override
    public void disableFiq() {
        cpsr |= FiqMask;
    }
    
    @Override
    public void setCpuState(CpuState newState) {
        if (newState == CpuState.Thumb)
            cpsr |= StateMask;
        else
            cpsr &= ~StateMask;
    }
    
    @Override
    public CpuMode getCpuMode() {
        return cpuMode;
    }
    
    @Override
    public void setCpuMode(CpuMode mode) {
        // Don't do anything when changing to the same mode
        if (cpuMode == mode) return;
        
        int src = cpuMode.ordinal();
        int dst = mode.ordinal();
        for (int i = 0; i < Arm7Register.RegisterSize; ++i) {
            regSet[src][i] = R[i];
            R[i] = regSet[dst][i];
        }
        spsrSet[dst] = cpsr;
        cpsr = (cpsr & ~ModeMask) | ModeBits[dst];
        cpuMode = mode;
    }
}
