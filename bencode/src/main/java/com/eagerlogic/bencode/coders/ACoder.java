/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

import com.eagerlogic.bencode.AValue;
import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @author dipacs
 */
public abstract class ACoder<T extends AValue<?>> {
    
    public T decode(String bencodedString) throws IOException, CodingException {
        return decode(new StringReader(bencodedString));
    }
    
    public String encode(T value) throws CodingException {
        StringBuilder sb = new StringBuilder();
        encode(value, sb);
        return sb.toString();
    }
    
    public abstract T decode(StringReader reader) throws IOException, CodingException;
    public abstract void encode(T value, StringBuilder sb) throws CodingException;
    public abstract boolean canDecode(StringReader reader) throws IOException;
    
}
