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
    CpuCycle execute(Cpu cpu, int[] operands);
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
    // Format 0 (no such format and is used solely for undefined opcode)
    UNDEFINED("???") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            // TODO
            return null;
        }
    },
    // Format 1 - 0: LSL Rd, Rs, #immed
    LSL_REG_IMMED("LSL") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
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
            return CpuCycle.CODE_S1;
        }
    },
    // Format 1 - 1: LSR Rd, Rs, #immed
    LSR_REG_IMMED("LSR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
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
            return CpuCycle.CODE_S1;
        }
    },
    // Format 1 - 2: ASR Rd, Rs, #immed
    ASR_REG_IMMED("ASR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
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
            return CpuCycle.CODE_S1;
        }
    },
    // Format 2 - 0: ADD Rd, Rs, Rn
    ADD_REG_REG("ADD") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            int rd = operands[0];
            int rs = operands[1];
            int rn = operands[2];
            int rsValue = cpu.getRegister(rs);
            int rnValue = cpu.getRegister(rn);
            int rdValue = rsValue + rnValue;
            cpu.setRegister(rd, rdValue);
            cpu.setZeroFlag(rdValue == 0);
            cpu.setSignedFlag(rdValue < 0);
            long unsignedValue = intToUnsignedLong(rsValue) + intToUnsignedLong(rnValue);
            cpu.setCarryFlag(unsignedValue > MAX_UNSIGNED_INT);
            long fullValue = ((long) rsValue) + rnValue;
            cpu.setOverflowFlag((fullValue > Integer.MAX_VALUE) || (fullValue < Integer.MIN_VALUE));
            return CpuCycle.CODE_S1;
        }
    },
    // Format 2 - 1: SUB Rd, Rs, Rn
    SUB_REG_REG("SUB") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 2 - 2: ADD Rd, Rs, #immed
    ADD_REG_IMMED("ADD") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 2 - 3: SUB Rd, Rs, #immed
    SUB_REG_IMMED("SUB") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 3 - 0: MOV Rd, #immed
    MOV_IMMED("MOV") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 3 - 1: CMP Rd, #immed
    CMP_IMMED("CMP") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 3 - 2: ADD Rd, #immed
    ADD_IMMED("ADD") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 3 - 3: SUB Rd, #immed
    SUB_IMMED("SUB") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }
    },
    // Format 4 - 0: AND Rd, Rs
    AND_REG("AND") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 1: EOR Rd, Rs
    EOR_REG("EOR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 2: LSL Rd, Rs
    LSL_REG("LSL") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 3: LSR Rd, Rs
    LSR_REG("LSR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 4: ASR Rd, Rs
    ASR_REG("ASR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 5: ADC Rd, Rs
    ADC_REG("ADC") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 6: SBC Rd, Rs
    SBC_REG("SBC") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 7: ROR Rd, Rs
    ROR_REG("ROR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 8: TST Rd, Rs
    TST_REG("TST") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 9: NEG Rd, Rs
    NEG_REG("NEG") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 10: CMP Rd, Rs
    CMP_REG("CMP") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 11: CMN Rd, Rs
    CMN_REG("CMN") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 12: ORR Rd, Rs
    ORR_REG("ORR") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 13: MUL Rd, Rs
    MUL_REG("MUL") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 14: BIC Rd, Rs
    BIC_REG("BIC") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    },
    // Format 4 - 15: MVN Rd, Rs
    MVN_REG("MVN") {
        @Override
        public CpuCycle execute(Cpu cpu, int[] operands) {
            return new CpuCycle(0, 1, 0, 0, 0);
        }        
    }
    ;
    
    private final static long MAX_UNSIGNED_INT = (1L << Integer.SIZE) - 1;
    private final static long INTEGER_MASK = MAX_UNSIGNED_INT;
    public static final int FORMAT_SIZE = 20;  // include UNDEFINED
    public static final Opcode[][] formatTab;
    
    static {
        formatTab = new Opcode[FORMAT_SIZE][];
        formatTab[0] = new Opcode[] { UNDEFINED };
        formatTab[1] = new Opcode[] {
            LSL_REG_IMMED, LSR_REG_IMMED, ASR_REG_IMMED
        };
        formatTab[2] = new Opcode[] {
            ADD_REG_REG, SUB_REG_REG, ADD_REG_IMMED, SUB_REG_IMMED
        };
        formatTab[3] = new Opcode[] {
            MOV_IMMED, CMP_IMMED, ADD_IMMED, SUB_IMMED
        };
        formatTab[4] = new Opcode[] {
            AND_REG, EOR_REG, LSL_REG, LSR_REG,
            ASR_REG, ADC_REG, SBC_REG, ROR_REG,
            TST_REG, NEG_REG, CMP_REG, CMN_REG,
            ORR_REG, MUL_REG, BIC_REG, MVN_REG
        };
    }
    
    private String shortName;
    private String extraName;
    
    ThumbOpcode(String sn) {
        shortName = sn;
    }
    
    static long intToUnsignedLong(int v) {
        return ((long) v) & INTEGER_MASK;
    }
    
    @Override
    public abstract CpuCycle execute(Cpu cpu, int[] operands);
    
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
