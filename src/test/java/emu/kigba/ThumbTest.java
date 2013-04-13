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
import static org.mockito.Matchers.anyInt;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.InOrder;

/**
 *
 * @author ricky
 */
public class ThumbTest {
    Arm7Register mockedRegister;
    MemoryManager mockedMM;
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
        cpu = new Thumb(mockedRegister, mockedMM);
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
            "AND", "XOR", "LSL", "LSR",
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
        int targetAddr = ((addrPc + 4) & ~2) + (offset << 2);
        assertOpcodeWithTwoOperands(op, "LDR", "PcRel", dst, targetAddr);
    }

    @Test
    public void decodeOpcodeUndefined() {
        // undefined instruction
        int instr = 0xFFFF;
        when(mockedMM.fetchHalfWord(0)).thenReturn(instr);
        cpu.fetch();
        Opcode op = cpu.decode();
        assertOpcodeWithNoOperands(op, "???", "");
    }
    
}