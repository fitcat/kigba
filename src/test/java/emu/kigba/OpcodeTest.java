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
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ricky
 */
public class OpcodeTest {
    Arm7Register mockedRegister;
    MemoryManager mockedMM;
    Cpu cpu;
    static java.util.Random rand;
    static int[] operands;
    
    public OpcodeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        rand = new java.util.Random();
        operands = new int[3];
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockedRegister = mock(Arm7Register.class);
        mockedMM = mock(MemoryManager.class);
        cpu = new Thumb(mockedRegister, mockedMM);
    }
    
    @After
    public void tearDown() {
    }

    private OpcodeTest verifyZero(boolean zf) {
        if (zf)
            verify(mockedRegister).setZero();
        else
            verify(mockedRegister).clearZero();
        return this;
    }

    private OpcodeTest verifySigned(boolean nf) {
        if (nf)
            verify(mockedRegister).setSigned();
        else
            verify(mockedRegister).clearSigned();
        return this;
    }

    private OpcodeTest verifyCarry(boolean cf) {
        if (cf)
            verify(mockedRegister).setCarry();
        else
            verify(mockedRegister).clearCarry();
        return this;
    }

    private OpcodeTest verifyOverflow(boolean vf) {
        if (vf)
            verify(mockedRegister).setOverflow();
        else
            verify(mockedRegister).clearOverflow();
        return this;
    }
    
    private OpcodeTest unchangeCarry() {
        verify(mockedRegister, never()).setCarry();
        verify(mockedRegister, never()).clearCarry();
        return this;
    }

    private OpcodeTest unchangeOverflow() {
        verify(mockedRegister, never()).setOverflow();
        verify(mockedRegister, never()).clearOverflow();
        return this;
    }
    
    @Test
    public void executeFormat_1_LSL_withNonZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);          // Rd
        operands[1] = rand.nextInt(8);          // Rs
        operands[2] = rand.nextInt(31) + 1;     // Immed (non-zero)
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.LSL_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = rsValue << operands[2];
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (32 - operands[2])) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }

    @Test
    public void executeFormat_1_LSL_withZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);   // Rd
        operands[1] = rand.nextInt(8);   // Rs
        operands[2] = 0;                // Immed = 0
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.LSL_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = rsValue << operands[2];
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        verifyZero(newZf).verifySigned(newNf).unchangeCarry().unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }
    
    @Test
    public void executeFormat_1_LSR_withNonZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);          // Rd
        operands[1] = rand.nextInt(8);          // Rs
        operands[2] = rand.nextInt(31) + 1;     // Immed (non-zero)
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.LSR_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = rsValue >>> operands[2];
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (operands[2] - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }

    @Test
    public void executeFormat_1_LSR_withZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);   // Rd
        operands[1] = rand.nextInt(8);   // Rs
        operands[2] = 0;                // Immed = 0
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.LSR_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = 0; // immed is zero means 32 => set to 0
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (operands[2] - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }
    
    @Test
    public void executeFormat_1_ASR_withNonZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);          // Rd
        operands[1] = rand.nextInt(8);          // Rs
        operands[2] = rand.nextInt(31) + 1;     // Immed (non-zero)
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.ASR_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = rsValue >> operands[2];
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf;
        int carry = (rsValue >>> (operands[2] - 1)) & 1;
        newCf = (carry == 1);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }

    @Test
    public void executeFormat_1_ASR_withZeroImmed() {
        // prepare the operands
        operands[0] = rand.nextInt(8);          // Rd
        operands[1] = rand.nextInt(8);          // Rs
        operands[2] = 0;                        // Immed = 0
        // prepare the value for Rs
        int rsValue = rand.nextInt();
        when(mockedRegister.get(operands[1])).thenReturn(rsValue);
        // execute SUT
        CpuCycle cc = ThumbOpcode.ASR_REG_IMMED.execute(cpu, operands);
        // verify the shifted result
        int expect = 0; // immed is zero means 32 => set to 0
        verify(mockedRegister).set(operands[0], expect);
        // verify the flags
        boolean newZf = (expect == 0);
        boolean newNf = (expect < 0);
        boolean newCf = (rsValue < 0);
        verifyZero(newZf).verifySigned(newNf).verifyCarry(newCf).unchangeOverflow();
        // verify cycles taken
        assertEquals(CpuCycle.CODE_S1, cc);
    }
    
    private void verifyFlags(InOrder inOrder, boolean[] zf, boolean[] nf, boolean[] cf, boolean[] vf) {
        for (int i = 0; i < zf.length; ++i) {
            if (zf[i])
                inOrder.verify(mockedRegister).setZero();
            else
                inOrder.verify(mockedRegister).clearZero();
            if (nf[i])
                inOrder.verify(mockedRegister).setSigned();
            else
                inOrder.verify(mockedRegister).clearSigned();
            if (cf[i])
                inOrder.verify(mockedRegister).setCarry();
            else
                inOrder.verify(mockedRegister).clearCarry();
            if (vf[i])
                inOrder.verify(mockedRegister).setOverflow();
            else
                inOrder.verify(mockedRegister).clearOverflow();
        }
    }
    
    @Test
    public void executeFormat_2_ADD_Register_Flag_N() {
        // prepare the operands
        operands[0] = 1;          // Rd
        operands[1] = 2;          // Rs
        operands[2] = 3;          // Rn
        // prepare the value for Rs and Rn
        // each flag consists of 2 sets of data - one for clear and one for set
        int[] rsValue = {
            1, 0,               // Z
            -1, -100,           // N
            3, -1,              // C
            3, 0x7FFFFFFF,      // V
        };
        int[] rnValue = {
            2, 0,               // Z
            3, 99,              // N
            4, -2,              // C
            -2, 2,              // V
        };
        int[] expect = {
            3, 0,
            2, -1,
            7, -3,
            1, 0x80000001,
        };
        boolean[] zf = {
            false, true,
            false, false,
            false, false,
            false, false,
        };
        boolean[] nf = {
            false, false,
            false, true,
            false, true,
            false, true, 
        };
        boolean[] cf = {
            false, false,
            true, false,
            false, true,
            true, false,
        };
        boolean[] vf = {
            false, false,
            false, false,
            false, false,
            false, true,
        };
        InOrder inOrder = inOrder(mockedRegister);
        for (int i = 0; i < rsValue.length; ++i) {
            when(mockedRegister.get(operands[1])).thenReturn(rsValue[i]);
            when(mockedRegister.get(operands[2])).thenReturn(rnValue[i]);
            // execute SUT
            CpuCycle cc = ThumbOpcode.ADD_REG_REG.execute(cpu, operands);
            // verify the shifted result
            verify(mockedRegister).set(operands[0], expect[i]);
            // verify cycles taken
            assertEquals(CpuCycle.CODE_S1, cc);
        }
        // verify flags
        verifyFlags(inOrder, zf, nf, cf, vf);
    }
}