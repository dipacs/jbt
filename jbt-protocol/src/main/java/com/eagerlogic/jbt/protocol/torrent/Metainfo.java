/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.torrent;

import com.eagerlogic.bencode.AValue;
import com.eagerlogic.bencode.BEncode;
import com.eagerlogic.bencode.DictionaryValue;
import com.eagerlogic.bencode.IntegerValue;
import com.eagerlogic.bencode.ListValue;
import com.eagerlogic.bencode.StringValue;
import com.eagerlogic.bencode.coders.CodingException;
import com.eagerlogic.jbt.protocol.peer.Hash;
import com.eagerlogic.jbt.protocol.trackerclient.TrackerClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dipacs
 */
public class Metainfo {
    
    private String announceUrl;
    private String name;
    private int pieceLength;
    private List<Hash> pieces;
    private long length;
    private List<FileDescriptor> files;
    
    public Metainfo(File file) throws FileNotFoundException, CodingException {
        FileInputStream fis = new FileInputStream(file);
        init(fis);
    }
    
    public Metainfo(InputStream is) throws CodingException {
        init(is);
    }
    
    private void init(InputStream is) throws CodingException {
        try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\z")) {
            String content = scanner.next();
            try {
                DictionaryValue metainfo = ((DictionaryValue)BEncode.getInstance().decode(content));
                parseMetaInfo(metainfo);
            } catch (IOException | CodingException ex) {
                throw new CodingException("Invalid torrent file. Bencoding failed.", ex);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(TrackerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void parseMetaInfo(DictionaryValue metainfo) throws CodingException {
        parseAnnounceUrl(metainfo);
        parseInfo(metainfo);
    }
    
    private void parseAnnounceUrl(DictionaryValue metainfo) throws CodingException {
        StringValue announceUrlValue = (StringValue) metainfo.getValue().get("announce");
        if (announceUrlValue == null) {
            throw new CodingException("Invalid metainfo. Missing 'announce' field.");
        }
        this.announceUrl = announceUrlValue.getValue();
    }
    
    private void parseInfo(DictionaryValue metainfo) throws CodingException {
        DictionaryValue infoValue = (DictionaryValue) metainfo.getValue().get("info");
        if (infoValue == null) {
            throw new CodingException("Invalid metainfo. Missing 'info' field.");
        }
        
        parseName(infoValue);
        parsePieceLength(infoValue);
        parsePieces(infoValue);
        parseFileDescriptors(infoValue);
    }
    
    private void parseName(DictionaryValue infoValue) throws CodingException {
        StringValue nameValue = (StringValue) infoValue.getValue().get("announce");
        if (nameValue == null) {
            throw new CodingException("Invalid metainfo. Missing 'name' field.");
        }
        this.name = nameValue.getValue();
    }
    
    private void parsePieceLength(DictionaryValue infoValue) throws CodingException {
        IntegerValue pieceLengthValue = (IntegerValue) infoValue.getValue().get("piece length");
        if (pieceLengthValue == null) {
            throw new CodingException("Invalid metainfo. Missing 'piece length' field.");
        }
    }
    
    private void parsePieces(DictionaryValue infoValue) throws CodingException {
        StringValue pieceValue = (StringValue) infoValue.getValue().get("pieces");
        if (pieceValue == null) {
            throw new CodingException("Invalid metainfo. Missing 'pieces' field.");
        }
        String piecesStr = pieceValue.getValue();
        if (piecesStr.length() % 20 != 0) {
            throw new CodingException("Invalid metainfo. Invalid pieces filed. Invalid length.");
        }
        for (int i = 0; i < piecesStr.length() / 20; i++) {
            String pieceStr = piecesStr.substring(i * 20, (i + 1) * 20);
            byte[] piece;
            try {
                piece = pieceStr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            Hash pieceHash = new Hash(piece);
            pieces.add(pieceHash);
        }
    }
    
    private void parseFileDescriptors(DictionaryValue infoValue) throws CodingException {
        if (infoValue.getValue().containsKey("length")) {
            parseSingleFile(infoValue);
        } else if (infoValue.getValue().containsKey("files")) {
            parseMultiFile(infoValue);
        } else {
            throw new CodingException("Invalid metainfo. Missing 'length' or 'file' key.");
        }
    }
    
    private void parseSingleFile(DictionaryValue infoValue) {
        IntegerValue lengthValue = (IntegerValue) infoValue.getValue().get("length");
        this.length = lengthValue.getValue();
        
        FileDescriptor fd = new FileDescriptor("/" + name, length, 0);
        files.add(fd);
    }
    
    private void parseMultiFile(DictionaryValue infoValue) {
        ListValue filesValue = (ListValue) infoValue.getValue().get("files");
        List<AValue<?>> filesList = filesValue.getValue();
        long currentOffset = 0;
        for (AValue<?> value : filesList) {
            DictionaryValue fileDict = (DictionaryValue) value;
            FileDescriptor fd = parseFile(fileDict, currentOffset);
            files.add(fd);
            currentOffset += fd.getLength();
        }
        
        this.length = currentOffset;
    }
    
    private FileDescriptor parseFile(DictionaryValue fileValue, long offset) {
        long len = ((IntegerValue)fileValue.getValue().get("length")).getValue();
        List<AValue<?>> pathList = ((ListValue)fileValue.getValue().get("path")).getValue();
        
        String path = "/" + name;
        
        for (AValue<?> value : pathList) {
            path += "/" + ((StringValue)value).getValue();
        }
        
        return new FileDescriptor(path, len, offset);
    }

    public String getAnnounceUrl() {
        return announceUrl;
    }

    public String getName() {
        return name;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public List<Hash> getPieces() {
        return pieces;
    }

    public long getLength() {
        return length;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }
    
    public boolean isMultiFile() {
        return files.size() > 1;
    }
    
}
