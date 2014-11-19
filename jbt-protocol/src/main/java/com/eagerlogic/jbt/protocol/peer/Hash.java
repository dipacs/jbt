/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.peer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 *
 * @author dipacs
 */
public class Hash {
    
    public static final Hash hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = digest.digest(data);
            return new Hash(hashedBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private final byte[] bytes;
    private final String str;

    public Hash(byte[] bytes) {
        if (bytes.length != 20) {
            throw new IllegalArgumentException("The length of the 'bytes' parameter must be 20.");
        }
        this.bytes = bytes;
        this.str = bytesToString(bytes);
    }
    
    public Hash(String hashString) {
        this.str = hashString.toUpperCase();
        this.bytes = stringToBytes(str);
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
        return res.toUpperCase();
    }
    
    private String byteToString(byte b) {
        String res = Integer.toHexString(b & 0xff);
        if (res.length() < 2) {
            res = "0" + res;
        }
        return res;
    }
    
    private byte[] stringToBytes(String str) {
        if (str.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hash string length.");
        }
        byte[] res = new byte[str.length() / 2];
        char[] chars = str.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            res[i] = (byte) ((hexCharToByte(chars[i * 2]) << 4) + hexCharToByte(chars[i * 2 + 1]));
        }
        return res;
    }
    
    private int hexCharToByte(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'A' && c <= 'F') {
            return c - 65;
        } else if (c >= 'a' && c <= 'f') {
            return c - 97;
        } else {
            throw new IllegalArgumentException("The given character is not a hex character: " + c);
        }
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
