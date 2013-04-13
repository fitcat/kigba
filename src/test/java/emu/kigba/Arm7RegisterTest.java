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

/**
 *
 * @author ricky
 */
public class Arm7RegisterTest {
    private Arm7Register r;
    
    public Arm7RegisterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        r = new Arm7Register();
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
    public void initialState() {
        assertEquals("PC = 00000000h when initialize", 0, r.get(Arm7Register.PC));
        assertEquals("Should be in ARM state", CpuState.Arm, r.getCpuState());
        assertEquals("FIQ should be disabled", false, r.isFiq());
        assertEquals("IRQ should be disabled", false, r.isIrq());
        assertEquals("Should be in supervisor mode", CpuMode.Supervisor, r.getCpuMode());
    }
    
    @Test
    public void R8toR12inFiqHasItsOwnRegister() {
        r.setCpuMode(CpuMode.Supervisor);  // Supervisor mode initially
        int value = 1234;
        for (int i = 8; i <= 12; ++i) {
            r.set(i, value);                // set Ri to 0x1234
            r.setCpuMode(CpuMode.Fiq);      // switch to FIQ mode
            r.set(i, 4321);                 // set Ri_fiq to other value
            r.setCpuMode(CpuMode.Supervisor);  // switch to Supervisor mode
            assertEquals("Changing R{8-12}_fiq should not affect R{8-12}", value, r.get(i));
        }
    }
    
    @Test
    public void R13toR14AreDifferentinEachMode() {
        int v1 = 1234, v2 = 5678;
        for (CpuMode src : CpuMode.values()) {
            r.setCpuMode(src);
            r.set(13, v1);
            r.set(14, v2);
            CpuMode dst = (src == CpuMode.Fiq) ? CpuMode.System : CpuMode.Fiq;
            r.setCpuMode(dst);
            r.set(13, 0);
            r.set(14, 0);
            r.setCpuMode(src);
            assertEquals("R13 should be reserved in mode switching " + src, v1, r.get(13));
            assertEquals("R14 should be reserved in mode switching " + src, v2, r.get(14));
        }
    }
    
    @Test
    public void setSignedFlagShouldAffectBit31InCpsr() {
        int signedMask = 1 << 31;
        r.setSigned();
        assertTrue("Bit 31 should be set when Signed flag on", (r.getCpsr() & signedMask) != 0);
        r.clearSigned();
        assertTrue("Bit 31 should be clear when Signed flag off", (r.getCpsr() & signedMask) == 0);
    }
    
    @Test
    public void setZeroFlagShouldAffectBit30InCpsr() {
        int zeroMask = 1 << 30;
        r.setZero();
        assertTrue("Bit 30 should be set when Zero flag on", (r.getCpsr() & zeroMask) != 0);
        r.clearZero();
        assertTrue("Bit 30 should be clear when Zero flag off", (r.getCpsr() & zeroMask) == 0);
    }
    
    @Test
    public void setCarryFlagShouldAffectBit29InCpsr() {
        int carryMask = 1 << 29;
        r.setCarry();
        assertTrue("Bit 29 should be set when Carry flag on", (r.getCpsr() & carryMask) != 0);
        r.clearCarry();
        assertTrue("Bit 29 should be clear when Carry flag off", (r.getCpsr() & carryMask) == 0);
    }
    
    @Test
    public void setOverflowFlagShouldAffectBit28InCpsr() {
        int overflowMask = 1 << 28;
        r.setOverflow();
        assertTrue("Bit 28 should be set when Overflow flag on", (r.getCpsr() & overflowMask) != 0);
        r.clearOverflow();
        assertTrue("Bit 28 should be clear when Overflow flag off", (r.getCpsr() & overflowMask) == 0);
    }
    
    @Test
    public void setIrqFlagShouldAffectBit7InCpsr() {
        int irqMask = 1 << 7;
        r.disableIrq();
        assertTrue("Bit 7 should be set when IRQ disabled", (r.getCpsr() & irqMask) != 0);
        r.enableIrq();
        assertTrue("Bit 7 should be clear when IRQ enabled", (r.getCpsr() & irqMask) == 0);
    }
    
    @Test
    public void setFiqFlagShouldAffectBit6InCpsr() {
        int fiqMask = 1 << 6;
        r.disableFiq();
        assertTrue("Bit 6 should be set when FIQ disabled", (r.getCpsr() & fiqMask) != 0);
        r.enableFiq();
        assertTrue("Bit 6 should be clear when FIQ enabled", (r.getCpsr() & fiqMask) == 0);
    }
    
    @Test
    public void setCpuStateShouldAffectBit5InCpsr() {
        int stateMask = 1 << 5;
        r.setCpuState(CpuState.Thumb);
        assertTrue("Bit 5 should be set when in THUMB state", (r.getCpsr() & stateMask) != 0);
        r.setCpuState(CpuState.Arm);
        assertTrue("Bit 5 should be clear when in ARM State", (r.getCpsr() & stateMask) == 0);
    }
    
    @Test
    public void setOperatingModeShouldAffectBit4To0InCpsr() {
        int modeMask = 0x1F;
        int[] expected = {
            0x1F /* System */, 0x10 /* User */, 0x11 /* FIQ */, 0x13 /* Supervisor */,
            0x17 /* Abort */, 0x12 /* IRQ */, 0x1B /* Undefined */, 
        };
        int i = 0;
        for (CpuMode m : CpuMode.values()) {
            r.setCpuMode(m);
            assertEquals("Bit 4-0 should be matched", expected[i++], r.getCpsr() & modeMask);
        }
    }
    
    @Test
    public void changeModeShouldSaveProgramStatusRegisters() {
        int oldCpsr = r.getCpsr();
        r.setCpuMode(CpuMode.Undefined);
        r.toggleSigned();
        r.toggleZero();
        r.toggleCarry();
        r.toggleOverflow();
        assertEquals("Original CPSR should be preserved in SPSR", oldCpsr, r.getSpsr());
    }
}