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
public class Peer {
    
    private final String clientId;
    private final String host;
    private final int port;

    public Peer(String clientId, String host, int port) {
        this.clientId = clientId;
        this.host = host;
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
}
