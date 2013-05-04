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

/**
 *
 * @author ricky
 */
public class CpuCycleTest {
    
    public CpuCycleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
        // reset all wait states to 0 (default for ROM access)
        setWaitState(1, 1, 1, 1);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    private static void setWaitState(int cn, int cs, int dn, int ds) {
        WaitState.CODE_N.set(cn);
        WaitState.CODE_S.set(cs);
        WaitState.DATA_N.set(dn);
        WaitState.DATA_S.set(ds);
    }

    @Test
    public void clockCycleConversion_Zero() {
        CpuCycle sut = new CpuCycle(0, 0, 0, 0, 0);
        assertEquals(0, sut.toClockCycle());
    }
    
    @Test
    public void clockCycleConversion_One() {
        CpuCycle sut = new CpuCycle(1, 1, 1, 1, 1);
        int ws_cn = 2, ws_cs = 3, ws_dn = 4, ws_ds = 5;
        setWaitState(ws_cn, ws_cs, ws_dn, ws_ds);
        int expectCodeNS = ws_cn;
        int expectCodeS = ws_cs;
        int expectDataNS = ws_dn;
        int expectDataS = ws_ds;
        int expect = expectCodeNS + expectCodeS + expectDataNS + expectDataS + 1;
        assertEquals(expect, sut.toClockCycle());
    }
    
    @Test
    public void clockCycleConversion_General() {
        int cn = 3, cs = 7, dn = 11, ds = 13, i = 23;
        CpuCycle sut = new CpuCycle(cn, cs, dn, ds, i);
        int ws_cn = 2, ws_cs = 3, ws_dn = 4, ws_ds = 5;
        setWaitState(ws_cn, ws_cs, ws_dn, ws_ds);
        int expectCodeNS = ws_cn * cn;
        int expectCodeS = ws_cs * cs;
        int expectDataNS = ws_dn * dn;
        int expectDataS = ws_ds * ds;
        int expect = expectCodeNS + expectCodeS + expectDataNS + expectDataS + i;
        assertEquals(expect, sut.toClockCycle());
    }
}