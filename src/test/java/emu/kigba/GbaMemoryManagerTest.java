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
public class GbaMemoryManagerTest {
    
    public GbaMemoryManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
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
    public void fetchWordTest() {
        byte[] model = {
            0x78, 0x56, 0x34, 0x12,
            0x10, 0x32, 0x54, 0x76,
            (byte) 0x89, (byte) 0xEF, (byte) 0xCD, (byte) 0xAB,
        };
        MemoryManager mm = new GbaMemoryManager(model);
        assertEquals("First word should be 0x12345678", 0x12345678, mm.fetchWord(0));
        assertEquals("Second word should be 0x76543210", 0x76543210, mm.fetchWord(4));
        assertEquals("Second word should be 0xABCDEF89", 0xABCDEF89, mm.fetchWord(8));
    }
    
    @Test
    public void fetchHalfWordTest() {
        byte[] model = { 0x34, 0x12, (byte) 0xCD, (byte) 0xAB };
        MemoryManager mm = new GbaMemoryManager(model);
        assertEquals("First half word should be 0x1234", 0x1234, mm.fetchHalfWord(0));
        assertEquals("Second half word should be 0xABCD", 0xABCD, mm.fetchHalfWord(2));
    }
}