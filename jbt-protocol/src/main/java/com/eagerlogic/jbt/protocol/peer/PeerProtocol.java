/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author dipacs
 */
public class PeerProtocol {

    private static final byte[] PROTOCOL_HEADER = "BitTorrent protocol".getBytes();
    private static final byte[] RESERVED_BYTES = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
    private static final byte[] KEEP_ALIVE_MSG = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};

    private final Socket socket;
    private final IPeerProtocolEventListener eventListener;
    private final BitField bitField;
    private final Hash torentInfoHash;
    private final Hash peerId;
    private Hash remotePeerId;
    private boolean choked = true;
    private boolean interested = false;
    private boolean remoteChoked = true;
    private boolean remoteInterested = false;
    private BitField remoteBitField;

    private final InputStream is;
    private final OutputStream os;

    public PeerProtocol(Socket socket, BitField bitField, Hash torrentInfoHash, Hash peerId, IPeerProtocolEventListener eventListener) throws IOException {
        this.socket = socket;
        this.bitField = bitField;
        this.torentInfoHash = torrentInfoHash;
        this.peerId = peerId;
        this.eventListener = eventListener;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();

        handShake();
    }

    public PeerProtocol(InetAddress host, int port, BitField bitField, Hash torrentInfoHash, Hash peerId, IPeerProtocolEventListener eventListener) throws IOException {
        socket = new Socket(host, port);
        this.bitField = bitField;
        this.torentInfoHash = torrentInfoHash;
        this.peerId = peerId;
        this.eventListener = eventListener;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();

        handShake();
        sendBitField();
        receiveBitField();
    }

    private void handShake() throws IOException {
        os.write(19);
        os.write(PROTOCOL_HEADER);
        os.write(RESERVED_BYTES);
        os.write(torentInfoHash.getBytes());
        os.write(peerId.getBytes());

        readBytes(1 + PROTOCOL_HEADER.length + RESERVED_BYTES.length);
        byte[] torrentInfoHashBytes = readBytes(20);
        Hash remoteTorrentInfoHash = new Hash(torrentInfoHashBytes);
        if (!this.torentInfoHash.equals(remoteTorrentInfoHash)) {
            try {
                socket.close();
            } catch (IOException ex) {
            }
            throw new IOException("The torrent info hash does not match.");
        }
        byte[] peerIdHashBytes = readBytes(20);
        Hash receivedPeerId = new Hash(peerIdHashBytes);
        if (!receivedPeerId.equals(this.remotePeerId)) {
            throw new IOException("Peer id does not match.");
        }
    }

    private void sendBitField() throws IOException {
        byte[] data = bitField.getBytes();
        writeInt(1 + data.length);
        os.write(5);
        os.write(data);
    }

    private void receiveBitField() throws IOException {
        int len = readInt();
        int messageId = is.read();
        if (messageId != 5) {
            throw new IOException("BitField message expected.");
        }
        byte[] bitFieldBytes = readBytes(len - 1);
        remoteBitField = new BitField(bitFieldBytes);
    }

    public BitField getBitField() {
        return bitField;
    }

    public Hash getTorentInfoHash() {
        return torentInfoHash;
    }

    public boolean isChoked() {
        return choked;
    }

    public boolean isInterested() {
        return interested;
    }

    public boolean isRemoteChoked() {
        return remoteChoked;
    }

    public boolean isRemoteInterested() {
        return remoteInterested;
    }

    public Hash getPeerId() {
        return peerId;
    }

    public Hash getRemotePeerId() {
        return remotePeerId;
    }

    public void sendChoke() throws IOException {
        this.choked = true;
        sendMessageWithoutPayload(0);
    }

    public void sendUnchoke() throws IOException {
        this.choked = false;
        sendMessageWithoutPayload(1);
    }

    public void sendInterested() throws IOException {
        this.interested = true;
        sendMessageWithoutPayload(2);
    }

    public void sendNotInterested() throws IOException {
        this.interested = false;
        sendMessageWithoutPayload(3);
    }

    public void sendHave(int pieceIndex) throws IOException {
        this.bitField.setBit(pieceIndex, true);
        writeInt(5);
        os.write(4);
        writeInt(pieceIndex);
    }

    public void sendRequest(int pieceIndex, int offset, int length) throws IOException {
        writeInt(13);
        os.write(6);
        writeInt(pieceIndex);
        writeInt(offset);
        writeInt(length);
    }

    public void sendPiece(int pieceIndex, int offset, byte[] data) throws IOException {
        writeInt(1 + 4 + 4 + data.length);
        os.write(7);
        writeInt(pieceIndex);
        writeInt(offset);
        os.write(data);
    }

    public void sendCancel(int pieceIndex, int offset, int length) throws IOException {
        writeInt(13);
        os.write(8);
        writeInt(pieceIndex);
        writeInt(offset);
        writeInt(length);
    }

    public void sendKeepAlive() throws IOException {
        os.write(KEEP_ALIVE_MSG);
    }

    public void close() throws IOException {
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isOpen() {
        return socket.isConnected();
    }

    private void sendMessageWithoutPayload(int messageId) throws IOException {
        writeInt(1);
        os.write(messageId & 0xff);
    }

    private void writeInt(int value) throws IOException {
        os.write((value >> 24) & 0xff);
        os.write((value >> 16) & 0xff);
        os.write((value >> 8) & 0xff);
        os.write((value) & 0xff);
    }

    private int readInt() throws IOException {
        return (is.read() << 24) | (is.read() << 16) | (is.read() << 8) | (is.read());
    }

    private byte[] readBytes(int count) throws IOException {
        return readBytes(new byte[count]);
    }

    private byte[] readBytes(byte[] bytes) throws IOException {
        int actReaded;
        int totalReaded = 0;
        while ((actReaded = is.read(bytes, totalReaded, bytes.length - totalReaded)) > 0) {
            totalReaded += actReaded;
        }
        return bytes;
    }

    public void poll() throws IOException {
        if (is.available() < 1) {
            return;
        }

        int len = readInt();
        if (len == 0) {
            this.eventListener.onKeepAlieveReceived();
            return;
        }
        if (len < 0) {
            throw new IOException("Invalid message length: " + len);
        }

        int messageId = is.read();
        switch (messageId) {
            case 0:
                receiveChoked(len - 1);
                break;
            case 1:
                receiveUnchoked(len - 1);
                break;
            case 2:
                receiveInterested(len - 1);
                break;
            case 3:
                receiveNotInterested(len - 1);
                break;
            case 4:
                receiveHave(len - 1);
                break;
            case 6:
                receiveRequest(len - 1);
                break;
            case 7:
                receivePiece(len - 1);
                break;
            case 8:
                receiveCancel(len - 1);
                break;
            default:
                throw new IOException("Invalid message id: " + messageId);
        }
    }

    private void receiveChoked(int dataLength) throws IOException {
        if (dataLength != 0) {
            throw new IOException("Invalid message length.");
        }
        this.remoteChoked = true;
        this.eventListener.onChokeReceived();
    }

    private void receiveUnchoked(int dataLength) throws IOException {
        if (dataLength != 0) {
            throw new IOException("Invalid message length.");
        }
        this.remoteChoked = false;
        this.eventListener.onUnchokeReceived();
    }

    private void receiveInterested(int dataLength) throws IOException {
        if (dataLength != 0) {
            throw new IOException("Invalid message length.");
        }
        this.remoteInterested = true;
        this.eventListener.onInterestedReceived();
    }

    private void receiveNotInterested(int dataLength) throws IOException {
        if (dataLength != 0) {
            throw new IOException("Invalid message length.");
        }
        this.remoteInterested = false;
        this.eventListener.onNotInterestedReceived();
    }

    private void receiveHave(int dataLength) throws IOException {
        if (dataLength != 4) {
            throw new IOException("Invalid message length.");
        }
        int pieceIndex = readInt();
        this.remoteBitField.setBit(pieceIndex, true);
        this.eventListener.onHaveReceived(pieceIndex);
    }

    private void receiveRequest(int dataLength) throws IOException {
        if (dataLength != 12) {
            throw new IOException("Invalid message length.");
        }
        int pieceIndex = readInt();
        int offset = readInt();
        int length = readInt();
        this.eventListener.onRequestReceived(pieceIndex, offset, length);
    }

    private void receivePiece(int dataLength) throws IOException {
        if (dataLength < 9) {
            throw new IOException("Invalid message length.");
        }
        int pieceIndex = readInt();
        int offset = readInt();
        byte[] data = readBytes(dataLength - 8);
        this.eventListener.onPieceReceived(pieceIndex, offset, data);
    }

    private void receiveCancel(int dataLength) throws IOException {
        if (dataLength != 12) {
            throw new IOException("Invalid message length.");
        }
        int pieceIndex = readInt();
        int offset = readInt();
        int length = readInt();
        this.eventListener.onCancelReceived(pieceIndex, offset, length);
    }

}
