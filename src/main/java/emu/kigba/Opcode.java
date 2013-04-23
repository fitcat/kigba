/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public interface Opcode {
    void execute(Cpu cpu, int[] operands);
    void setOperand(int dst, int left, int right);
    void setOperand(int dst, int src);
    void setOperand(int dst);
    int getOperandDst();
    int getOperandLeft();
    int getOperandRight();
    int getOperandSrc();
    String getShortName();
    String getExtraName();
}
