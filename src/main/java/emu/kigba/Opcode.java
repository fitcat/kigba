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

enum ThumbOpcode implements Opcode {
    LSL_RegImmed("LSL", "RegImmed") {
        @Override
        public void execute(Cpu cpu, int[] operands) {
            int rd = operands[0];
            int rs = operands[1];
            int immed = operands[2];
            int rsValue = cpu.getRegister(rs);
            int rdValue = rsValue << immed;
            cpu.setRegister(rd, rdValue);
            cpu.setZeroFlag(rdValue == 0);
            cpu.setSignedFlag(rdValue < 0);
            // Carry unchanged when shifted amount is zero
            if (immed != 0) {
                cpu.setCarryFlag(((rsValue >>> (32 - immed)) & 1) == 1);
            }
            cpu.addCycle(ArmCycle.S1);
        }
    },
    LSR_RegImmed("LSR", "RegImmed") {
        @Override
        public void execute(Cpu cpu, int[] operands) {
            int rd = operands[0];
            int rs = operands[1];
            int immed = operands[2];
            int rsValue = cpu.getRegister(rs);
            // when shifted amount is 0, it means 32, ie, set rdValue to 0
            int rdValue = 0;
            if (immed != 0)
                rdValue = rsValue >>> immed;
            cpu.setRegister(rd, rdValue);
            cpu.setZeroFlag(rdValue == 0);
            cpu.setSignedFlag(rdValue < 0);
            if (immed != 0) {
                cpu.setCarryFlag(((rsValue >>> (immed - 1)) & 1) == 1);
            }
            else {  // Carry flag equals to the msb
                cpu.setCarryFlag(rsValue < 0);  // msb means signed
            }
            cpu.addCycle(ArmCycle.S1);
        }
    },
    ASR_RegImmed("ASR", "RegImmed") {
        @Override
        public void execute(Cpu cpu, int[] operands) {
            int rd = operands[0];
            int rs = operands[1];
            int immed = operands[2];
            int rsValue = cpu.getRegister(rs);
            // when shifted amount is 0, it means 32, ie, set rdValue to 0
            int rdValue = 0;
            if (immed != 0)
                rdValue = rsValue >> immed;
            cpu.setRegister(rd, rdValue);
            cpu.setZeroFlag(rdValue == 0);
            cpu.setSignedFlag(rdValue < 0);
            if (immed != 0) {
                cpu.setCarryFlag(((rsValue >>> (immed - 1)) & 1) == 1);
            }
            else {  // Carry flag equals to the msb
                cpu.setCarryFlag(rsValue < 0);  // msb means signed
            }
            cpu.addCycle(ArmCycle.S1);
        }
    };
    
    
    private String shortName;
    private String extraName;
    
    ThumbOpcode(String sn, String en) {
        shortName = sn;
        extraName = en;
    }
    
    public abstract void execute(Cpu cpu, int[] operands);
    
    @Override
    public void setOperand(int dst, int left, int right) {
        
    }
    
    @Override
    public void setOperand(int dst, int src) {
        
    }
    
    @Override
    public void setOperand(int dst) {
        
    }
    
    @Override
    public int getOperandDst() {
        return 0;
    }
    
    @Override
    public int getOperandLeft() {
        return 0;
    }
    
    @Override
    public int getOperandRight() {
        return 0;
    }
    
    @Override
    public int getOperandSrc() {
        return 0;
    }
    
    @Override
    public String getShortName() {
        return shortName;
    }
    
    @Override
    public String getExtraName() {
        return extraName;
    }
};
