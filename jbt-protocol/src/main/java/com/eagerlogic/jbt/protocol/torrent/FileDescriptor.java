/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.torrent;

/**
 *
 * @author dipacs
 */
public class FileDescriptor {
    
    private final String path;
    private final long length;
    private final long offset;

    public FileDescriptor(String path, long length, long offset) {
        this.path = path;
        this.length = length;
        this.offset = offset;
    }

    public String getPath() {
        return path;
    }

    public long getLength() {
        return length;
    }

    public long getOffset() {
        return offset;
    }
    
}
