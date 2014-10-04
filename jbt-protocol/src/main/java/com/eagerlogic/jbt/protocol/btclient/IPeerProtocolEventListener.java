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
public interface IPeerProtocolEventListener {
    
    public void onChokeReceived();
    public void onUnchokeReceived();
    public void onInterestedReceived();
    public void onNotInterestedReceived();
    public void onHaveReceived(int pieceIndex);
    public void onRequestReceived(int pieceIndex, int offset, int length);
    public void onPieceReceived(int pieceIndex, int offset, byte[] data);
    public void onCancelReceived(int pieceIndex, int offset, int length);
    public void onKeepAlieveReceived();
    
}
