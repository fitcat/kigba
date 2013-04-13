/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public class Thumb implements Cpu {
    final Opcode Undefined    = new OpcodeUndefined();
    final Opcode [][] opcodeFormat = {
        /*  0 */ {Undefined}, 
        /*  1 */ {new OpcodeLSL_RegImmed(), new OpcodeLSR_RegImmed(), new OpcodeASR_RegImmed()},
        /*  2 */ {new OpcodeADD_RegReg(), new OpcodeSUB_RegReg(),
                  new OpcodeADD_RegImmed(), new OpcodeSUB_RegImmed(),
                 },
        /*  3 */ {new OpcodeMOV_Immed(), new OpcodeCMP_Immed(),
                  new OpcodeADD_Immed(), new OpcodeSUB_Immed(),
                 },
        /*  4 */ {new OpcodeAND_Reg(), new OpcodeXOR_Reg(),
                  new OpcodeLSL_Reg(), new OpcodeLSR_Reg(),
                  new OpcodeASR_Reg(), new OpcodeADC_Reg(),
                  new OpcodeSBC_Reg(), new OpcodeROR_Reg(),
                  new OpcodeTST_Reg(), new OpcodeNEG_Reg(),
                  new OpcodeCMP_Reg(), new OpcodeCMN_Reg(),
                  new OpcodeORR_Reg(), new OpcodeMUL_Reg(),
                  new OpcodeBIC_Reg(), new OpcodeMVN_Reg(),
                 },
        /*  5 */ {new OpcodeADD_HiReg(), new OpcodeCMP_HiReg(),
                  new OpcodeMOV_HiReg(), new OpcodeBX(),
                 },
        /*  6 */ {new OpcodeLDR_PcRel()},
        /*  7 */ {new OpcodeSTR_RegReg(), new OpcodeSTRB_RegReg(),
                  new OpcodeLDR_RegReg(), new OpcodeLDRB_RegReg(),
                 },
    };
    
    private Arm7Register register;
    private MemoryManager memMgr;
    private int instr;      // the current instruction
    private int addr;       // the current address of instr

    private abstract class BasicOpcode {
        int dst, left, right;   // operands
        final String shortName;
        final String extraName;
        
        protected BasicOpcode() {
            String name = this.getClass().getSimpleName();
            String fullName = name.substring("Opcode".length());
            int underscorePos = fullName.indexOf('_');
            if (underscorePos == -1) {
                shortName = fullName;
                extraName = "";
            }
            else {
                shortName = fullName.substring(0, underscorePos);
                extraName = fullName.substring(underscorePos+1);
            }
        }
        
        public void execute() {
            throw new UnsupportedOperationException("Method execute() must be defined.");
        }
        
        public void setOperand(int dst, int left, int right) {
            this.dst = dst;
            this.left = left;
            this.right = right;
        }
        
        public void setOperand(int dst, int src) {
            this.dst = dst;
            this.left = src;
        }
        
        public void setOperand(int dst) {
            this.dst = dst;
        }
        
        public int getOperandDst() {
            return dst;
        }
        
        public int getOperandLeft() {
            return left;
        }
        
        public int getOperandRight() {
            return right;
        }
        
        public int getOperandSrc() {
            return left;
        }
        
        public String getShortName() {
            return shortName;
        }

        public String getExtraName() {
            return extraName;
        }       
    }
    
    private class OpcodeLSL_RegImmed extends BasicOpcode implements Opcode {
       
        @Override
        public void execute() {
        }
    }

    private class OpcodeLSR_RegImmed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }
    
    private class OpcodeASR_RegImmed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }
    
    private class OpcodeADD_RegReg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeADD_RegImmed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
            
        }
    }
    
    private class OpcodeSUB_RegReg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }
    
    private class OpcodeSUB_RegImmed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeMOV_Immed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeCMP_Immed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeADD_Immed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeSUB_Immed extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeAND_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeXOR_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeLSL_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeLSR_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeASR_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeADC_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeSBC_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeROR_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeTST_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeNEG_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeCMP_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeCMN_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeORR_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeMUL_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeBIC_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeMVN_Reg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeADD_HiReg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeCMP_HiReg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeMOV_HiReg extends BasicOpcode implements Opcode {
        
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeBX extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeLDR_PcRel extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeSTR_RegReg extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeSTRB_RegReg extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeLDR_RegReg extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeLDRB_RegReg extends BasicOpcode implements Opcode {
        @Override
        public void execute() {
        
        }
    }

    private class OpcodeUndefined extends BasicOpcode implements Opcode {
        @Override
        public String getShortName() {
            return "???";
        }

        @Override
        public String getExtraName() {
            return "";
        }       
                
        @Override
        public void execute() {
        }
    }
    
    public Thumb(Arm7Register r, MemoryManager m) {
        register = r;
        memMgr = m;
    }
    
    @Override
    public void fetch() {
        int currentPc = getRegister(Arm7Register.PC);
        addr = currentPc;
        instr = memMgr.fetchHalfWord(currentPc);
        setRegister(Arm7Register.PC, currentPc + 2);
    }
    
    private Opcode decodeFormat_1() {
        int bit12_11 = (instr >> 11) & 0x3;
        Opcode result = opcodeFormat[1][bit12_11];
        int immed = (instr >> 6) & 0b11111;
        int src = (instr >> 3) & 0b111;
        int dst = instr & 0b111;
        result.setOperand(dst, src, immed);
        return result;
    }
    
    private Opcode decodeFormat_2() {
        int bit10_9 = (instr >> 9) & 0b11;
        Opcode result = opcodeFormat[2][bit10_9];
        int dst = instr & 0b111;
        int left = (instr >> 3) & 0b111;
        int right = (instr >> 6) & 0b111;
        result.setOperand(dst, left, right);
        return result;
    }
    
    private Opcode decodeFormat_1_2() {
        int bit12_11 = (instr >> 11) & 3;
        if (bit12_11 != 0b11)
            return decodeFormat_1();
        return decodeFormat_2();
    }
    
    private Opcode decodeFormat_3() {
        int bit12_11 = (instr >> 11) & 3;
        Opcode result = opcodeFormat[3][bit12_11];
        int dst = (instr >> 8) & 7;
        int src = instr & 0xFF;
        result.setOperand(dst, src);
        return result;
    }
    
    private Opcode decodeFormat_4() {
        int bit9_6 = (instr >> 6) & 0xF;
        Opcode result = opcodeFormat[4][bit9_6];
        int dst = instr & 7;
        int src = (instr >> 3) & 7;
        result.setOperand(dst, src);
        return result;
    }
    
    private Opcode decodeFormat_5() {
        int msb = (instr >>> 6) & 3;
        // Both MSBd and MSBs cannot be clear for ADD/CMP/MOV
        if (((instr >>> 8) & 3) != 3) { // ADD/CMP/MOV
            if (msb == 0)
                return Undefined;
        }
        else {  // BX must have MSBd clear
            if (msb > 1)
                return Undefined;
        }
        int bit9_8 = (instr >>> 8) & 3;
        Opcode result = opcodeFormat[5][bit9_8];
        int dst = (instr & 7) + ((instr >>> 4) & 8);
        int src = (instr >>> 3) & 15;
        result.setOperand(dst, src);
        return result;
    }
    
    private Opcode decodeFormat_6() {
        int dst = (instr >>> 8) & 7;
        int offset = (instr & 0xFF) << 2;
        int newPc = (addr + 4) & ~2;
        Opcode result = opcodeFormat[6][0];
        result.setOperand(dst, newPc + offset);
        return result;
    }
    
    private Opcode decodeFormat_7() {
        int right = (instr >>> 6) & 7;
        int left = (instr >>> 3) & 7;
        int dst = instr & 7;
        int bit11_10 = (instr >> 10) & 3;
        Opcode result = opcodeFormat[7][bit11_10];
        result.setOperand(dst, left, right);
        return result;
    }
    
    private Opcode decodeFormat_8() {
        return null;
    }
    
    private Opcode decodeFormat_9() {
        return null;
    }
    
    private Opcode decodeFormat_10() {
        return null;
    }
    
    private Opcode decodeFormat_11() {
        return null;
    }
    
    private Opcode decodeFormat_12() {
        return null;
    }
    
    private Opcode decodeFormat_13() {
        return null;
    }
    
    private Opcode decodeFormat_14() {
        return null;
    }
    
    private Opcode decodeFormat_15() {
        return null;
    }
    
    private Opcode decodeFormat_16() {
        return null;
    }
    
    private Opcode decodeFormat_17() {
        return null;
    }
    
    private Opcode decodeFormat_18() {
        return null;
    }
    
    private Opcode decodeFormat_19() {
        return null;
    }
    
    private Opcode decodeFormat_4_5() {
        if ((instr & (1 << 10)) == 0)
            return decodeFormat_4();
        return decodeFormat_5();
    }
    
    private Opcode decodeFormat_4_to_6() {
        if ((instr & (1 << 11)) == 0)
            return decodeFormat_4_5();
        return decodeFormat_6();
    }
    
    private Opcode decodeFormat_7_8() {
        if ((instr & (1 << 9)) == 0)
            return decodeFormat_7();
        return decodeFormat_8();
    }
    
    private Opcode decodeFormat_4_to_8() {
        if ((instr & (1 << 12)) == 0)
            return decodeFormat_4_to_6();
        return decodeFormat_7_8();
    }
    
    @Override
    public Opcode decode() {
        int bit15_13 = (instr >> 13) & 7;
        switch (bit15_13) {
            case 0: return decodeFormat_1_2();
            case 1: return decodeFormat_3();
            case 2: return decodeFormat_4_to_8();
            
        }
        return Undefined;
    }
    
    @Override
    public int getInstr() {
        return instr;
    }
    
    @Override
    public int getRegister(int which) {
        return register.get(which);
    }
    
    @Override
    public void setRegister(int which, int value) {
        register.set(which, value);
    }
    
    @Override
    public void setCpuMode(CpuMode newMode) {
        register.setCpuMode(newMode);
    }
}
