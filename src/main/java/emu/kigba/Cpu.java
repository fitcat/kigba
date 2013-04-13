/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public interface Cpu {
    void fetch();
    Opcode decode();
    int getInstr();
    int getRegister(int which);
    void setRegister(int which, int value);
    void setCpuMode(CpuMode newMode);
}
