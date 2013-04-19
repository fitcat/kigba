/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

/**
 *
 * @author ricky
 */
public class ThumbTest {
    Arm7Register mockedRegister;
    MemoryManager mockedMM;
    ArmCycle mockedCycle;
    Cpu cpu;
    java.util.Random rand;
    
    public ThumbTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockedRegister = mock(Arm7Register.class);
        mockedMM = mock(MemoryManager.class);
        mockedCycle = mock(ArmCycle.class);
        cpu = new Thumb(mockedRegister, mockedMM, mockedCycle);
        rand = new java.util.Random();
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void fetchShouldIncrementPCby2() {
        final int addrPc = 1234;
        when(mockedRegister.get(Arm7Register.PC)).thenReturn(addrPc);
        cpu.fetch();
        verify(mockedRegister).set(Arm7Register.PC, addrPc + 2);
    }
    
    @Test
    public void fetchOpcodeTest() {
        when(mockedMM.fetchHalfWord(0)).thenReturn(0x1234);
        when(mockedMM.fetchHalfWord(2)).thenReturn(0x4321);
        when(mockedRegister.get(Arm7Register.PC))
            .thenReturn(0)
            .thenReturn(2);
        cpu.fetch();
        assertEquals("First opcode should be 0x1234", 0x1234, cpu.getInstr());
        cpu.fetch();
        assertEquals("Second opcode should be 0x4321", 0x4321, cpu.getInstr());
    }

    @Test
    public void setCpuModeShouldCallRegister() {
        for (CpuMode m : CpuMode.values()) {
            mockedRegister.setCpuMode(m);
        }
        InOrder inOrder = inOrder(mockedRegister);
        for (CpuMode m : CpuMode.values()) {
            cpu.setCpuMode(m);
        }
        for (CpuMode m : CpuMode.values()) {
            inOrder.verify(mockedRegister).setCpuMode(m);
        }
    }
    
    private void assertOpcodeWithThreeOperands(Opcode op, String shortName, String extraName, int dst, int left, int right) {
        assertOpcodeWithTwoOperands(op, shortName, extraName, dst, left);
        assertEquals("Operand right should match", right, op.getOperandRight());
    }

    private void assertOpcodeWithTwoOperands(Opcode op, String shortName, String extraName, int dst, int src) {
        assertOpcodeWithOneOperand(op, shortName, extraName, dst);
        assertEquals("Opernad left should match", src, op.getOperandSrc());
    }   
    
    private void assertOpcodeWithOneOperand(Opcode op, String shortName, String extraName, int dst) {
        assertOpcodeWithNoOperands(op, shortName, extraName);
        assertEquals("Operand dst should match", dst, op.getOperandDst());
    }
    
    private void assertOpcodeWithNoOperands(Opcode op, String shortName, String extraName) {
        assertEquals("Opcode short name should match", shortName, op.getShortName());
        assertEquals("Opcode extra name should match", extraName, op.getExtraName());
    }
    
    @Test
    public void decodeFormat_1() {
        final String[] name = {
            "LSL", "LSR", "ASR",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int src = rand.nextInt(8);
            int immed = rand.nextInt(32);
            int instr = (i << 11) | (immed << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithThreeOperands(op, name[i], "RegImmed", dst, src, immed);
        }
    }

    @Test
    public void decodeFormat_2() {
        final String[] name = {
            "ADD", "SUB", "ADD", "SUB",
        };
        final String[] extraName = {
            "RegReg", "RegImmed",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int left = rand.nextInt(8);
            int right = rand.nextInt(8);
            int instr = (0b00011 << 11) | (i << 9) | (right << 6) | (left << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithThreeOperands(op, name[i], extraName[i >> 1], dst, left, right);
        }
    }

    @Test
    public void decodeFormat_3() {
        final String[] name = {
            "MOV", "CMP", "ADD", "SUB",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int src = rand.nextInt(256);
            int instr = (0b001 << 13) | (i << 11) | (dst << 8) | src;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithTwoOperands(op, name[i], "Immed", dst, src);
        }
    }

    @Test
    public void decodeFormat_4() {
        final String[] name = {
            "AND", "EOR", "LSL", "LSR",
            "ASR", "ADC", "SBC", "ROR",
            "TST", "NEG", "CMP", "CMN",
            "ORR", "MUL", "BIC", "MVN",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int src = rand.nextInt(8);
            int instr = (0b010000 << 10) | (i << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithTwoOperands(op, name[i], "Reg", dst, src);
        }
    }

    private void testDecodeBX() {
        int dst = rand.nextInt(8);  // don't care value
        int src = rand.nextInt(8);
        for (int i = 0; i < 2; ++i) {
            int instr = (0b010001 << 10) | (0b11 << 8) | (i << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithNoOperands(op, "BX", "");
            assertEquals("Src must match", src + (i << 3), op.getOperandSrc());
        }
    }
    
    @Test
    public void decodeFormat_5() {
        final String[] name = {
            "ADD", "CMP", "MOV",
        };
        final int[] msb = {1, 2, 3};    // MSBs and MSBd must not be all 0
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int src = rand.nextInt(8);
            int instr = (0b010001 << 10) | (i << 8) | (msb[i] << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithTwoOperands(op, name[i], "HiReg",
                    dst + ((msb[i] & 2) == 0 ? 0 : 8),
                    src + ((msb[i] & 1) == 0 ? 0 : 8));
        }
        // Special handle for BX
        testDecodeBX();
    }
    
    @Test
    public void decodeFormat_5_Inavlid() {
        // Invalid opcode format for ADD/CMP/MOV
        int dst = rand.nextInt(8);
        int src = rand.nextInt(8);
        int msb = 0;    // invlaid as either MSBd or MSBs must be set
        for (int i = 0; i < 3; ++i) {
            int instr = (0b010001 << 10) | (i << 8) | (msb << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithNoOperands(op, "???", "");
        }
        // Invalid opcode format for BX: MSBd is set
        for (int i = 0b10; i <= 0b11; ++i) {
            int instr = (0b010001 << 10) | (0b11 << 8) | (i << 6) | (src << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithNoOperands(op, "???", "");
        }
    }
    
    @Test
    public void decodeOpcodeFormat_6() {
        final int addrPc = 1236;
        when(mockedRegister.get(Arm7Register.PC)).thenReturn(addrPc);
        int dst = rand.nextInt(8);
        int offset = rand.nextInt(256);
        int instr = (0b01001 << 11) | (dst << 8) | offset;
        when(mockedMM.fetchHalfWord(addrPc)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();
        offset <<= 2;   // step in 4
        assertOpcodeWithTwoOperands(op, "LDR", "PcRel", dst, offset);
    }

    @Test
    public void decodeFormat_7() {
        final String[] name = {
            "STR", "STRB", "LDR", "LDRB",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int left = rand.nextInt(8);
            int right = rand.nextInt(8);
            int instr = (0b0101 << 12) | (i << 10) | (right << 6) | (left << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithThreeOperands(op, name[i], "RegReg", dst, left, right);
        }
    }

    @Test
    public void decodeFormat_8() {
        final String[] name = {
            "STRH", "LDSB", "LDRH", "LDSH",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int left = rand.nextInt(8);
            int right = rand.nextInt(8);
            int instr = (0b0101 << 12) | (i << 10) | 1 << 9 | (right << 6) | (left << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithThreeOperands(op, name[i], "RegReg", dst, left, right);
        }
    }

    @Test
    public void decodeFormat_9() {
        final String[] name = {
            "STR", "LDR", "STRB", "LDRB",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int reg = rand.nextInt(8);
            int immed = rand.nextInt(32);
            int instr = (0b011 << 13) | (i << 11) | (immed << 6) | (reg << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            immed <<= (i < 2 ? 2 : 0);  // step in 4 for WORD accesses
            assertOpcodeWithThreeOperands(op, name[i], "RegImmed", dst, reg, immed);
        }
    }

    @Test
    public void decodeFormat_10() {
        final String[] name = {
            "STRH", "LDRH",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int reg = rand.nextInt(8);
            int immed = rand.nextInt(32);
            int instr = (0b1000 << 12) | (i << 11) | (immed << 6) | (reg << 3) | dst;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            immed <<= 1;    // step in 2 for HALFWORD accesses
            assertOpcodeWithThreeOperands(op, name[i], "RegImmed", dst, reg, immed);
        }
    }

    @Test
    public void decodeFormat_11() {
        final String[] name = {
            "STR", "LDR",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int immed = rand.nextInt(256);
            int instr = (0b1001 << 12) | (i << 11) | (dst << 8) | immed;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            immed <<= 2;        // step in 4 for WORD accesses
            assertOpcodeWithTwoOperands(op, name[i], "SpRel", dst, immed);
        }
    }

    @Test
    public void decodeFormat_12() {
        final String[] name = {
            "ADD", "ADD",
        };
        final String[] extraName = {
            "PcRel", "SpRel",
        };
        for (int i = 0; i < name.length; ++i) {
            int dst = rand.nextInt(8);
            int immed = rand.nextInt(256);
            int instr = (0b1010 << 12) | (i << 11) | (dst << 8) | immed;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            immed <<= 2;        // step in 4
            assertOpcodeWithTwoOperands(op, name[i], extraName[i], dst, immed);
        }
    }

    @Test
    public void decodeFormat_13() {
        final String[] name = {
            "ADD", "ADD",
        };
        final String[] extraName = {
            "SpInc", "SpDec",
        };
        for (int i = 0; i < name.length; ++i) {
            int immed = rand.nextInt(128);
            int instr = (0b10110000 << 8) | (i << 7) | immed;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            immed <<= 2;        // step in 4
            assertOpcodeWithOneOperand(op, name[i], extraName[i], immed);
        }
    }

    @Test
    public void decodeFormat_13_Invalid() {
        int mask = ~4;      // bit 10 must be clear
        for (int i = 1; i < 16; ++i) {
            int immed = rand.nextInt(128);
            int signed = rand.nextInt(2);
            int bit_11_8 = (i & mask) << 8;
            if (bit_11_8 == 0) continue;    // only non-zero for bit 8-11 are invalid
            int instr = (0b1011 << 12) | bit_11_8 | (signed << 7) | immed;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithNoOperands(op, "???", "");
        }
    }

    @Test
    public void decodeFormat_14() {
        final String[] name = {
            "PUSH", "POP", "PUSH", "POP",
        };
        final String[] extraName = {
            "Reg", "Reg", "RegLr", "RegPc",
        };
        for (int i = 0; i < name.length; ++i) {
            int regList = rand.nextInt(256);
            int bit_11 = i & 1;   // 0 - PUSH, 1 - POP
            int bit_8 = i >>> 1;  // 0 - no, 1 - LR/PC for PUSH/POP
            int instr = (0b1011 << 12) | (bit_11 << 11) | (0b10 << 9) | (bit_8 << 8) | regList;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithOneOperand(op, name[i], extraName[i], regList);
        }
    }

    @Test
    public void decodeFormat_14_Invalid() {
        for (int i = 0; i < 4; ++i) {
            int regList = rand.nextInt(256);
            int bit_11 = i & 1;   // 0 - PUSH, 1 - POP
            int bit_8 = i >>> 1;  // 0 - no, 1 - LR/PC for PUSH/POP
            // bit 9 is set which is invalid
            int instr = (0b1011 << 12) | (bit_11 << 11) | (0b11 << 9) | (bit_8 << 8) | regList;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithNoOperands(op, "???", "");
        }
    }

    @Test
    public void decodeFormat_15() {
        final String[] name = {
            "STMIA", "LDMIA",
        };
        for (int i = 0; i < name.length; ++i) {
            int regBase = rand.nextInt(8);
            int regList = rand.nextInt(256);
            int instr = (0b1100 << 12) | (i << 11) | (regBase << 8) | regList;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithTwoOperands(op, name[i], "", regBase, regList);
        }
    }

    @Test
    public void decodeFormat_16() {
        final String[] name = {
            "BEQ", "BNE", "BCS", "BCC",
            "BMI", "BPL", "BVS", "BVC",
            "BHI", "BLS", "BGE", "BLT",
            "BGT", "BLE", 
        };
        for (int i = 0; i < name.length; ++i) {
            int offset = rand.nextInt(256);
            int instr = (0b1101 << 12) | (i << 8) | offset;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithOneOperand(op, name[i], "", offset);
        }
    }

    @Test
    public void decodeFormat_16_Invalid() {
        int offset = rand.nextInt(256);
        // bit 8-11 (cond) equals 14 is invalid
        int instr = (0b1101 << 12) | (14 << 8) | offset;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "???", "");
    }

    @Test
    public void decodeFormat_17() {
        int swi = rand.nextInt(256);
        int instr = (0b11011111 << 8) | swi;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "SWI", "");
    }

    @Test
    public void decodeFormat_18() {
        final int [] offset = {
            0, 2, 1023, 1024 /* -1024 */, 2047 /* -1 */
        };
        final int [] expect = {
            0, 4, 2046, -2048, -2,
        };
        for (int i = 0; i < offset.length; ++i) {
            int target = offset[i];
            int instr = (0b11100 << 11) | target;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithOneOperand(op, "B", "", expect[i]);
        }
    }

    @Test
    public void decodeFormat_18_Invalid() {
        // bit 11 is set meaning invalid
        int instr = (0b1110 << 12) | (1 << 11) | rand.nextInt(2048);
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "???", "");
    }

    @Test
    public void decodeFormat_19() {
        final int [] offset = {
            0, 2, 0x1FFFFF, 0x200000 /* -0x200000 */, 0x3FFFFF /* -1 */
        };
        final int [] expect = {
            0, 4, 0x3FFFFE, -0x400000, -2,
        };
        for (int i = 0; i < offset.length; ++i) {
            int target = offset[i];
            int targetLo = target >>> 11;
            int instrLo = (0b11110 << 11) | targetLo;
            int targetHi = target & 0x7FF;
            int instrHi = (0b11111 << 11) | targetHi;
            when(mockedMM.fetchHalfWord(0)).thenReturn(instrLo).thenReturn(instrHi);
            cpu.fetch();
            Opcode op = cpu.decode();
            assertOpcodeWithOneOperand(op, "BL", "", expect[i]);
        }
    }

    @Test
    public void decodeFormat_19_InvalidInFirstInstruction() {
        // First instruction bit 11 is set meaning invalid
        int instrLo = (0b11111 << 11) | rand.nextInt(0x800);
        // Second instruction bit 11 is set meaning valid
        int instrHi = (0b11111 << 11) | rand.nextInt(0x800);
        when(mockedMM.fetchHalfWord(0)).thenReturn(instrLo).thenReturn(instrHi);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "???", "");
    }

    @Test
    public void decodeFormat_19_InvalidInSecondInstruction() {
        // First instruction bit 11 is clear meaning valid
        int instrLo = (0b11110 << 11) | rand.nextInt(0x800);
        // Second instruction bit 11 is clear meaning invalid
        int instrHi = (0b11110 << 11) | rand.nextInt(0x800);
        when(mockedMM.fetchHalfWord(0)).thenReturn(instrLo).thenReturn(instrHi);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "???", "");
    }
    
    private ThumbTest verifyZero(boolean zf) {
        if (zf)
            verify(mockedRegister).setZero();
        else
            verify(mockedRegister).clearZero();
        return this;
    }

    private ThumbTest verifySigned(boolean nf) {
        if (nf)
            verify(mockedRegister).setSigned();
        else
            verify(mockedRegister).clearSigned();
        return this;
    }

    private ThumbTest verifyCarry(boolean cf) {
        if (cf)
            verify(mockedRegister).setCarry();
        else
            verify(mockedRegister).clearCarry();
        return this;
    }

    private ThumbTest verifyOverflow(boolean vf) {
        if (vf)
            verify(mockedRegister).setOverflow();
        else
            verify(mockedRegister).clearOverflow();
        return this;
    }
    
    private ThumbTest unchangeCarry() {
        verify(mockedRegister, never()).setCarry();
        verify(mockedRegister, never()).clearCarry();
        return this;
    }

    private ThumbTest unchangeOverflow() {
        verify(mockedRegister, never()).setOverflow();
        verify(mockedRegister, never()).clearOverflow();
        return this;
    }
    
    @Test
    public void executeFormat_1_LSL_withNonZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = rand.nextInt(31) + 1;
        int instr = (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = rsValue << immed;
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (32 - immed)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }

    @Test
    public void executeFormat_1_LSL_withZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = 0;
        int instr = (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = rsValue << immed;
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        verifyZero(newZf).verifySigned(newNf).unchangeCarry().unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }
    
    @Test
    public void executeFormat_1_LSR_withNonZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = rand.nextInt(31) + 1;
        int instr = (1 << 11) | (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = rsValue >>> immed;
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (immed - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }

    @Test
    public void executeFormat_1_LSR_withZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = 0;
        int instr = (1 << 11) | (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = 0; // immed is zero means 32 => set to 0
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (immed - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }
    
    @Test
    public void executeFormat_1_ASR_withNonZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = rand.nextInt(31) + 1;
        int instr = (1 << 12) | (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = rsValue >> immed;
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (immed - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }

    @Test
    public void executeFormat_1_ASR_withZeroImmed() {
        // prepare the instruction
        int rd = rand.nextInt(8);
        int rs = rand.nextInt(8);
        int immed = 0;
        int instr = (1 << 12) | (immed << 6) | (rs << 3) | rd;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();

        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(rs)).thenReturn(rsValue);
        
        // execute
        op.execute();
        
        // verify the shifted result
        int expect = 0; // immed is zero means 32 => set to 0
        verify(mockedRegister).set(rd, expect);
        
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >> (immed - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        
        // verify cycles taken
        verify(mockedCycle).add(ArmCycle.S1);
    }
    
}