/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.btclient;

import java.util.Objects;

/**
 *
 * @author dipacs
 */
public class Hash {
    
    private final byte[] bytes;
    private final String str;

    public Hash(byte[] bytes) {
        if (bytes.length != 20) {
            throw new IllegalArgumentException("The length of the 'bytes' parameter must be 20.");
        }
        this.bytes = bytes;
        this.str = bytesToString(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return str;
    }
    
    private String bytesToString(byte[] bytes) {
        String res = "";
        for (byte b : bytes) {
            res += byteToString(b);
        }
        return res;
    }
    
    private String byteToString(byte b) {
        String res = Integer.toHexString(b & 0xff);
        if (res.length() < 2) {
            res = "0" + res;
        }
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Hash)) {
            return false;
        }
        Hash other = (Hash) obj;
        
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.str);
        return hash;
    }
    
}
