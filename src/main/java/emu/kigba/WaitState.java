/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 * This class represents the wait states for accessing code and data in memory.
 * Both code and data divide into non-sequential and sequential categories.
 * Thus, there are 4 elements in total.
 * Since the actual clock cycle is (1 + wait state), the value adds one
 * to the actual wait states for faster processing, i.e.,
 * if actual wait state is 0, the value stored is 1.
 * @author ricky
 */
public class WaitState {
    public static WaitState CODE_N, CODE_S, DATA_N, DATA_S;
    static {
        CODE_N = new WaitState(1);
        CODE_S = new WaitState(1);
        DATA_N = new WaitState(1);
        DATA_S = new WaitState(1);
    }
    private int value;
    
    public WaitState(int n) {
        value = n;
    }
    public void set(int n) { value = n; }
    public int get() { return value; }
}