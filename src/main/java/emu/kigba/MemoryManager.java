/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;


/**
 *
 * @author ricky
 */
public interface MemoryManager {
    int fetchWord(int addr);
    int fetchHalfWord(int addr);
}
