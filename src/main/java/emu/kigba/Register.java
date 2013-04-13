/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public interface Register {

    void clearCarry();

    void clearOverflow();

    void clearSigned();

    void clearZero();

    void disableFiq();

    void disableIrq();

    void enableFiq();

    void enableIrq();

    int get(int which);

    int getCpsr();

    CpuMode getCpuMode();

    CpuState getCpuState();

    int getSpsr();

    boolean isCarry();

    boolean isFiq();

    boolean isIrq();

    boolean isOverflow();

    boolean isSigned();

    boolean isZero();

    void set(int which, int value);

    void setCarry();

    void setCpuMode(CpuMode mode);

    void setCpuState(CpuState newState);

    void setOverflow();

    void setSigned();

    void setZero();

    void toggleCarry();

    void toggleOverflow();

    void toggleSigned();

    void toggleZero();
    
}
