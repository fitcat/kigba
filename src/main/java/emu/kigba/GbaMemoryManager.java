/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public class GbaMemoryManager implements MemoryManager {
    
    private byte[] model;
    
    public GbaMemoryManager(byte[] model) {
        this.model = model;
    }
    
    @Override
    public int fetchWord(int addr) {
        int result = 0;
        for (int i = 3; i >= 0; --i) {
            result <<= 8;
            result += model[addr + i] & 0xFF;
        }
        return result;
    }
    
    @Override
    public int fetchHalfWord(int addr) {
        int result = 0;
        for (int i = 1; i >= 0; --i) {
            result <<= 8;
            result += model[addr + i] & 0xFF;
        }
        return result;
    }
    
}
