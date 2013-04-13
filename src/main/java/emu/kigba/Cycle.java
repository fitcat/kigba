/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emu.kigba;

/**
 *
 * @author ricky
 */
public final class Cycle {
    private final int value;
    
    public Cycle(int n, int s, int i, int c) {
        value = (n << 8) + (s << 8) + (i << 8) + (c << 8);
    }
    
    public int cn() { 
        return value >> 24;
    }
    
    public int cs() {
        return (value >> 16) & 0xFF;
    }
    
    public int ci() {
        return (value >> 8) & 0xFF;
    }
    
    public int cc() {
        return value & 0xFF;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Cycle)) return false;
        return hashCode() == o.hashCode();
    }
    
    @Override
    public int hashCode() {
        return value;
    }
    
}
