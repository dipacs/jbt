/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.trackerclient;

/**
 *
 * @author dipacs
 */
public class TrackerResponse {
    
    private final int interval;
    private final Peer[] peers;

    public TrackerResponse(int interval, Peer[] peers) {
        this.interval = interval;
        this.peers = peers;
    }

    public int getInterval() {
        return interval;
    }

    public Peer[] getPeers() {
        return peers;
    }
    
}
