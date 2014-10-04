/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.trackerclient;

import com.eagerlogic.bencode.AValue;
import com.eagerlogic.bencode.BEncode;
import com.eagerlogic.bencode.DictionaryValue;
import com.eagerlogic.bencode.IntegerValue;
import com.eagerlogic.bencode.ListValue;
import com.eagerlogic.bencode.StringValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dipacs
 */
public class TrackerClient {

    public static enum EEvent {
        
        STARTED("started"),
        COMPLETED("completed"),
        STOPPED("stopped");
        
        private final String value;
        
        private EEvent(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
        
    }
    
    public static TrackerResponse announce(String announceUrl, String infoHash, String peerId, String ip, int listeningPort, int uploaded, int downloaded, int left, EEvent event) throws TrackerException, MalformedURLException, IOException {
        String url = announceUrl + "?";
        
        url += "?info_hash=" + urlEncode(infoHash);
        url += "&peer_id=" + urlEncode(peerId);
        if (ip != null) {
            url += "&ip=" + urlEncode(ip);
        }
        url += "&port=" + listeningPort;
        url += "&uploaded=" + uploaded;
        url += "&downloaded=" + downloaded;
        url += "&left=" + left;
        if (event != null) {
            url += "&event=" + urlEncode(event.getValue());
        }
        
        URL u = new URL(url);
        InputStream is = null;
        Scanner scanner = null;
        try {
            is = u.openStream();
            scanner = new Scanner(is, "UTF-8").useDelimiter("\\z");
            String content = scanner.next();
            try {
                DictionaryValue res = ((DictionaryValue)BEncode.getInstance().decode(content));
                if (res.getValue().containsKey("failure reason")) {
                    return parseError(res);
                } else {
                    return parseResult(res);
                }
            } catch (Exception ex) {
                throw new TrackerException("Invalid response. Beencoding failed.", ex);
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(TrackerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private static TrackerResponse parseError(DictionaryValue res) throws TrackerException {
        throw new TrackerException((String) res.getValue().get("failure reason").getValue());
    }

    private static TrackerResponse parseResult(DictionaryValue res) throws TrackerException {
        int interval = parseInterval(res);
        Peer[] peers = parsePeers(res);
        return new TrackerResponse(interval, peers);
    }

    private static int parseInterval(DictionaryValue res) throws TrackerException {
        IntegerValue intervalValue = (IntegerValue) res.getValue().get("interval");
        if (intervalValue == null) {
            throw new TrackerException("Invalid tracker response. Missing 'interval' key.");
        }
        return intervalValue.getValue();
    }
    
    private static Peer[] parsePeers(DictionaryValue res) throws TrackerException {
        AValue<?> peersValue = res.getValue().get("peers");
        if (peersValue instanceof StringValue) {
            return parseCompactPeers((StringValue) peersValue);
        } else {
            return parsePeersList((ListValue) peersValue);
        }
    }
    
    private static Peer[] parsePeersList(ListValue peersList) {
        List<Peer> resList = new ArrayList<>();
        for (AValue<?> value : peersList.getValue()) {
            DictionaryValue peerValue = (DictionaryValue) value;
            resList.add(parsePeer(peerValue));
        }
        return resList.toArray(new Peer[resList.size()]);
    }
    
    private static Peer[] parseCompactPeers(StringValue peersString) throws TrackerException {
        String peerListStr = peersString.getValue();
        byte[] data = peerListStr.getBytes(Charset.forName("UTF-8"));
        if ((data.length % 6) != 0) {
            throw new TrackerException("Invalid response. Compacted peer list length mismatch");
        }
        
        List<Peer> resList = new ArrayList<>();
        for (int i = 0; i < data.length / 6; i = i + 6) {
            resList.add(parsePeer(data, i * 6));
        }
        return resList.toArray(new Peer[resList.size()]);
    }
    
    private static Peer parsePeer(byte[] data, int offset) {
        String host = (data[offset] & 0xff) + "." + (data[offset + 1] & 0xff) + "." + (data[offset + 2] & 0xff) + "." + (data[offset + 3] & 0xff);
        int port = ((data[offset + 4] & 0xff) << 8) | (data[offset + 5] & 0xff);
        return new Peer(null, host, port);
    }
    
    private static Peer parsePeer(DictionaryValue peerValue) {
        String peerId = (String) peerValue.getValue().get("peer id").getValue();
        String ip = (String) peerValue.getValue().get("ip").getValue();
        int port = ((Integer)peerValue.getValue().get("port").getValue());
        return new Peer(peerId, ip, port);
    }
    
    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private TrackerClient() {
    }
    
}
