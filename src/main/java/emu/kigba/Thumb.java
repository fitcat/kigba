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
        /*  4 */ {new OpcodeAND_Reg(), new OpcodeEOR_Reg(),
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
        /*  8 */ {new OpcodeSTRH_RegReg(), new OpcodeLDSB_RegReg(),
                  new OpcodeLDRH_RegReg(), new OpcodeLDSH_RegReg(),
                 },
        /*  9 */ {new OpcodeSTR_RegImmed(), new OpcodeLDR_RegImmed(),
                  new OpcodeSTRB_RegImmed(), new OpcodeLDRB_RegImmed(),
                 },
        /* 10 */ {new OpcodeSTRH_RegImmed(), new OpcodeLDRH_RegImmed()},
        /* 11 */ {new OpcodeSTR_SpRel(), new OpcodeLDR_SpRel()},
        /* 12 */ {new OpcodeADD_PcRel(), new OpcodeADD_SpRel()},
        /* 13 */ {new OpcodeADD_SpInc(), new OpcodeADD_SpDec()},
        /* 14 */ {new OpcodePUSH_Reg(), new OpcodePOP_Reg(),
                  new OpcodePUSH_RegLr(), new OpcodePOP_RegPc(),
                 },
        /* 15 */ {new OpcodeSTMIA(), new OpcodeLDMIA()},
        /* 16 */ {new OpcodeBEQ(), new OpcodeBNE(),
                  new OpcodeBCS(), new OpcodeBCC(),
                  new OpcodeBMI(), new OpcodeBPL(),
                  new OpcodeBVS(), new OpcodeBVC(),
                  new OpcodeBHI(), new OpcodeBLS(),
                  new OpcodeBGE(), new OpcodeBLT(),
                  new OpcodeBGT(), new OpcodeBLE(),
                 },
        /* 17 */ {new OpcodeSWI()},
        /* 18 */ {new OpcodeB()},
        /* 19 */ {new OpcodeBL()},
    };

    
    private Arm7Register register;
    private MemoryManager memMgr;
    private Cycle cycle;
    private int instr;      // the current instruction
    private int[] operands; // the operands of the current instruction

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
        
        public void execute(Cpu cpu, int[] operands) {
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
    }

    private class OpcodeLSR_RegImmed extends BasicOpcode implements Opcode {
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
    }
    
    private class OpcodeASR_RegImmed extends BasicOpcode implements Opcode {
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
    }
    
    private class OpcodeADD_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_RegImmed extends BasicOpcode implements Opcode {
    }
    
    private class OpcodeSUB_RegReg extends BasicOpcode implements Opcode {
    }
    
    private class OpcodeSUB_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeMOV_Immed extends BasicOpcode implements Opcode {
    }

    private class OpcodeCMP_Immed extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_Immed extends BasicOpcode implements Opcode {
    }

    private class OpcodeSUB_Immed extends BasicOpcode implements Opcode {
    }

    private class OpcodeAND_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeEOR_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLSL_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLSR_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeASR_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeADC_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeSBC_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeROR_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeTST_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeNEG_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeCMP_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeCMN_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeORR_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeMUL_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeBIC_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeMVN_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_HiReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeCMP_HiReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeMOV_HiReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeBX extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDR_PcRel extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTR_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTRB_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDR_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDRB_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTRH_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDSB_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDRH_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDSH_RegReg extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTR_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDR_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTRB_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDRB_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTRH_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDRH_RegImmed extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTR_SpRel extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDR_SpRel extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_PcRel extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_SpRel extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_SpInc extends BasicOpcode implements Opcode {
    }

    private class OpcodeADD_SpDec extends BasicOpcode implements Opcode {
    }

    private class OpcodePUSH_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodePOP_Reg extends BasicOpcode implements Opcode {
    }

    private class OpcodePUSH_RegLr extends BasicOpcode implements Opcode {
    }

    private class OpcodePOP_RegPc extends BasicOpcode implements Opcode {
    }

    private class OpcodeSTMIA extends BasicOpcode implements Opcode {
    }

    private class OpcodeLDMIA extends BasicOpcode implements Opcode {
    }

    private class OpcodeBEQ extends BasicOpcode implements Opcode {
    }

    private class OpcodeBNE extends BasicOpcode implements Opcode {
    }

    private class OpcodeBCS extends BasicOpcode implements Opcode {
    }

    private class OpcodeBCC extends BasicOpcode implements Opcode {
    }

    private class OpcodeBMI extends BasicOpcode implements Opcode {
    }

    private class OpcodeBPL extends BasicOpcode implements Opcode {
    }

    private class OpcodeBVS extends BasicOpcode implements Opcode {
    }

    private class OpcodeBVC extends BasicOpcode implements Opcode {
    }

    private class OpcodeBHI extends BasicOpcode implements Opcode {
    }

    private class OpcodeBLS extends BasicOpcode implements Opcode {
    }

    private class OpcodeBGE extends BasicOpcode implements Opcode {
    }

    private class OpcodeBLT extends BasicOpcode implements Opcode {
    }

    private class OpcodeBGT extends BasicOpcode implements Opcode {
    }

    private class OpcodeBLE extends BasicOpcode implements Opcode {
    }

    private class OpcodeSWI extends BasicOpcode implements Opcode {
    }

    private class OpcodeB extends BasicOpcode implements Opcode {
    }

    private class OpcodeBL extends BasicOpcode implements Opcode {
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
    }
    
    public Thumb(Arm7Register reg, MemoryManager mm, Cycle cycle) {
        register = reg;
        memMgr = mm;
        this.cycle = cycle;
        operands = new int[3];
    }
    
    @Override
    public void fetch() {
        int currentPc = getRegister(Arm7Register.PC);
        instr = memMgr.fetchHalfWord(currentPc);
        setRegister(Arm7Register.PC, currentPc + 2);
    }
    
    private Opcode decodeFormat_1(int[] operands) {
        int bit12_11 = (instr >>> 11) & 0x3;
        Opcode result = opcodeFormat[1][bit12_11];
        operands[0] = instr & 0b111;
        operands[1] = (instr >>> 3) & 0b111;
        operands[2] = (instr >>> 6) & 0b11111;
        return result;
    }
    
    private Opcode decodeFormat_2(int[] operands) {
        int bit10_9 = (instr >>> 9) & 0b11;
        Opcode result = opcodeFormat[2][bit10_9];
        int dst = instr & 0b111;
        int left = (instr >>> 3) & 0b111;
        int right = (instr >>> 6) & 0b111;
        result.setOperand(dst, left, right);
        return result;
    }
    
    private Opcode decodeFormat_1_2(int[] operands) {
        int bit12_11 = (instr >>> 11) & 3;
        if (bit12_11 != 0b11)
            return decodeFormat_1(operands);
        return decodeFormat_2(operands);
    }
    
    private Opcode decodeFormat_3(int[] operands) {
        int bit12_11 = (instr >>> 11) & 3;
        Opcode result = opcodeFormat[3][bit12_11];
        int dst = (instr >>> 8) & 7;
        int src = instr & 0xFF;
        result.setOperand(dst, src);
        return result;
    }
    
    private Opcode decodeFormat_4(int[] operands) {
        int bit9_6 = (instr >>> 6) & 0xF;
        Opcode result = opcodeFormat[4][bit9_6];
        int dst = instr & 7;
        int src = (instr >>> 3) & 7;
        result.setOperand(dst, src);
        return result;
    }
    
    private Opcode decodeFormat_5(int[] operands) {
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
    
    private Opcode decodeFormat_6(int[] operands) {
        int dst = (instr >>> 8) & 7;
        int offset = (instr & 0xFF) << 2;   // step in 4
        Opcode result = opcodeFormat[6][0];
        result.setOperand(dst, offset);
        return result;
    }
    
    private Opcode decodeFormat_7(int[] operands) {
        int right = (instr >>> 6) & 7;
        int left = (instr >>> 3) & 7;
        int dst = instr & 7;
        int bit11_10 = (instr >>> 10) & 3;
        Opcode result = opcodeFormat[7][bit11_10];
        result.setOperand(dst, left, right);
        return result;
    }
    
    private Opcode decodeFormat_8(int[] operands) {
        int right = (instr >>> 6) & 7;
        int left = (instr >>> 3) & 7;
        int dst = instr & 7;
        int bit11_10 = (instr >>> 10) & 3;
        Opcode result = opcodeFormat[8][bit11_10];
        result.setOperand(dst, left, right);
        return result;
    }
    
    private Opcode decodeFormat_9(int[] operands) {
        int immed = (instr >>> 6) & 0x1F;
        int reg = (instr >>> 3) & 7;
        int dst = instr & 7;
        int bit12_11 = (instr >>> 11) & 3;
        Opcode result = opcodeFormat[9][bit12_11];
        immed <<= (bit12_11 < 2 ? 2 : 0);   // step of 4 for WORD
        result.setOperand(dst, reg, immed);
        return result;
    }
    
    private Opcode decodeFormat_10(int[] operands) {
        int immed = ((instr >>> 6) & 0x1F) << 1;    // in step of 2
        int reg = (instr >>> 3) & 7;
        int dst = instr & 7;
        int bit11 = (instr >>> 11) & 1;
        Opcode result = opcodeFormat[10][bit11];
        result.setOperand(dst, reg, immed);
        return result;
    }
    
    private Opcode decodeFormat_11(int[] operands) {
        int immed = (instr & 0xFF) << 2;    // in step of 4
        int dst = (instr >>>8) & 7;
        int bit11 = (instr >>> 11) & 1;
        Opcode result = opcodeFormat[11][bit11];
        result.setOperand(dst, immed);
        return result;
    }
    
    private Opcode decodeFormat_12(int[] operands) {
        int immed = (instr & 0xFF) << 2;    // in step of 4
        int dst = (instr >>> 8) & 7;
        int bit11 = (instr >>> 11) & 1;
        Opcode result = opcodeFormat[12][bit11];
        result.setOperand(dst, immed);
        return result;
    }
    
    private Opcode decodeFormat_13(int[] operands) {
        int bit_11_8 = (instr >>> 8) & 15;
        if (bit_11_8 != 0) return Undefined;
        int immed = (instr & 0x7F) << 2;    // in step of 4
        int bit7 = (instr >>> 7) & 1;
        Opcode result = opcodeFormat[13][bit7];
        result.setOperand(immed);
        return result;
    }
    
    private Opcode decodeFormat_14(int[] operands) {
        int bit_9 = (instr >>> 9) & 1;
        if (bit_9 != 0) return Undefined;
        int bit_11 = (instr >>> 11) & 1;
        int bit_8 = (instr >>> 8) & 1;
        int type = (bit_8 << 1) + bit_11;
        int regList = instr & 0xFF;
        Opcode result = opcodeFormat[14][type];
        result.setOperand(regList);
        return result;
    }
    
    private Opcode decodeFormat_15(int[] operands) {
        int regList = (instr & 0xFF);
        int regBase = (instr >>> 8) & 7;
        int bit11 = (instr >>> 11) & 1;
        Opcode result = opcodeFormat[15][bit11];
        result.setOperand(regBase, regList);
        return result;
    }
    
    private Opcode decodeFormat_16(int[] operands) {
        int cond = (instr >>> 8) & 0xF;
        if (cond == 0xE) return Undefined;
        int offset = instr & 0xFF;
        Opcode result = opcodeFormat[16][cond];
        result.setOperand(offset);
        return result;
    }
    
    private Opcode decodeFormat_17(int[] operands) {
        // the comment immediate field is ignored
        return opcodeFormat[17][0];
    }
    
    private Opcode decodeFormat_18(int[] operands) {
        int bit_11 = (instr >>> 11) & 1;
        if (bit_11 != 0)
            return Undefined;
        int offset = instr & 0x7FF;
        if (offset >= 1024) {     // offset is negative
            offset |= 0xFFFFF800; // set all upper bits
        }
        offset <<= 1;       // step in 2
        Opcode result = opcodeFormat[18][0];
        result.setOperand(offset);
        return result;
    }
    
    private Opcode decodeFormat_19(int[] operands) {
        int instrLo = instr;    // save the first instruction
        int lobit_11 = (instrLo >>> 11) & 1;
        // Bit 11 in first instruction must be clear
        if (lobit_11 != 0)
            return Undefined;
        fetch();                // fetch the second instruction
        int instrHi = instr;    // get the second instruction
        int hibit_11 = (instrHi >>> 11) & 1;
        // Bit 11 in second instruction must be set
        if (hibit_11 != 1)
            return Undefined;
        instr |= instrLo << 16; // store the 2 instructions together
        int offset = instrLo & 0x7FF;
        offset <<= 11;
        offset |= instrHi & 0x7FF;
        if (offset >= 0x200000) {   // offset is negative
            offset |= 0xFFC00000;   // set all upper bits
        }
        offset <<= 1;       // step in 2
        Opcode result = opcodeFormat[19][0];
        result.setOperand(offset);
        return result;
    }
    
    private Opcode decodeFormat_4_5(int[] operands) {
        if ((instr & (1 << 10)) == 0)
            return decodeFormat_4(operands);
        return decodeFormat_5(operands);
    }
    
    private Opcode decodeFormat_4_to_6(int[] operands) {
        if ((instr & (1 << 11)) == 0)
            return decodeFormat_4_5(operands);
        return decodeFormat_6(operands);
    }
    
    private Opcode decodeFormat_7_8(int[] operands) {
        if ((instr & (1 << 9)) == 0)
            return decodeFormat_7(operands);
        return decodeFormat_8(operands);
    }
    
    private Opcode decodeFormat_4_to_8(int[] operands) {
        if ((instr & (1 << 12)) == 0)
            return decodeFormat_4_to_6(operands);
        return decodeFormat_7_8(operands);
    }
    
    private Opcode decodeFormat_10_11(int[] operands) {
        if ((instr & (1 << 12)) == 0)
            return decodeFormat_10(operands);
        return decodeFormat_11(operands);
    }
    
    private Opcode decodeFormat_12_14(int[] operands) {
        if ((instr & (1 << 12)) == 0)
            return decodeFormat_12(operands);
        return decodeFormat_13_14(operands);
    }
    
    private Opcode decodeFormat_13_14(int[] operands) {
        if ((instr & (1 << 10)) == 0)
            return decodeFormat_13(operands);
        return decodeFormat_14(operands);
    }
    
    private Opcode decodeFormat_15_to_17(int[] operands) {
        if ((instr & (1 << 12)) == 0)
            return decodeFormat_15(operands);
        return decodeFormat_16_17(operands);
    }
    
    private Opcode decodeFormat_16_17(int[] operands) {
        int bit_8_11 = (instr >>> 8) & 0xF;
        if (bit_8_11 != 0b1111)
            return decodeFormat_16(operands);
        return decodeFormat_17(operands);
    }
    
    private Opcode decodeFormat_18_19(int[] operands) {
        int bit_12 = (instr >>> 12) & 1;
        if (bit_12 == 0)
            return decodeFormat_18(operands);
        return decodeFormat_19(operands);
    }
    
    @Override
    public Opcode decode(int[] operands) {
        int bit15_13 = (instr >> 13) & 7;
        switch (bit15_13) {
            case 0: return decodeFormat_1_2(operands);
            case 1: return decodeFormat_3(operands);
            case 2: return decodeFormat_4_to_8(operands);
            case 3: return decodeFormat_9(operands);
            case 4: return decodeFormat_10_11(operands);
            case 5: return decodeFormat_12_14(operands);
            case 6: return decodeFormat_15_to_17(operands);
            default:    // must be 7
                return decodeFormat_18_19(operands);
        }
    }
    
    @Override
    public int[] getOperands() {
        return operands;
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
    public void setZeroFlag(boolean zf) {
        if (zf)
            register.setZero();
        else
            register.clearZero();
    }
    
    @Override
    public void setSignedFlag(boolean nf) {
        if (nf)
            register.setSigned();
        else
            register.clearSigned();
    }
    
    @Override
    public void setCarryFlag(boolean zf) {
        if (zf)
            register.setCarry();
        else
            register.clearCarry();
    }
    
    @Override
    public void setOverflowFlag(boolean zf) {
        if (zf)
            register.setOverflow();
        else
            register.clearOverflow();
    }
    
    @Override
    public void setCpuMode(CpuMode newMode) {
        register.setCpuMode(newMode);
    }
    
    @Override
    public void addCycle(Cycle cyc) {
        cycle.add(cyc);
    }
}
