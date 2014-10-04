/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.btclient;

/**
 *
 * @author dipacs
 */
public class BitField {
    
    private static final int[] SET_MASKS = new int[] {
            0x80,
            0x40,
            0x20,
            0x10,
            0x08,
            0x04,
            0x02,
            0x01,
    };
    private static final int[] CLEAR_MASKS = new int[] {
            0x7f,
            0xbf,
            0xdf,
            0xef,
            0xf7,
            0xfb,
            0xfd,
            0xfe
    };
    
    private final byte[] bytes;
    
    public BitField(int bitCount) {
        int arrayLength = bitCount / 8;
        if (bitCount % 8 != 0) {
            arrayLength++;
        }
        bytes = new byte[arrayLength];
    }
    
    public BitField(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public void setBit(int index, boolean value) {
        if (value) {
            bytes[index / 8] = (byte) (bytes[index / 8] | SET_MASKS[index % 8]);
        } else {
            bytes[index / 8] = (byte) (bytes[index / 8] | CLEAR_MASKS[index % 8]);
        }
    }
    
    public boolean getBit(int index) {
        return (bytes[index / 8] & SET_MASKS[index % 8]) > 0;
    }

    public byte[] getBytes() {
        return bytes;
    }
    
}
