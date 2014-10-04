/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode;

import com.eagerlogic.bencode.coders.ACoder;
import com.eagerlogic.bencode.coders.CodingException;
import com.eagerlogic.bencode.coders.DictionaryCoder;
import com.eagerlogic.bencode.coders.IntegerCoder;
import com.eagerlogic.bencode.coders.ListCoder;
import com.eagerlogic.bencode.coders.StringCoder;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dipacs
 */
public class BEncode extends ACoder<AValue<?>> {
    
    private static BEncode instance;
    
    public static BEncode getInstance() {
        if (instance == null) {
            synchronized (BEncode.class) {
                if (instance == null) {
                    instance = new BEncode();
                }
            }
        }
        return instance;
    }
    
    private final Map<Class<? extends AValue>, ACoder> coders = new HashMap<>();

    private BEncode() {
        coders.put(IntegerValue.class, new IntegerCoder());
        coders.put(StringValue.class, new StringCoder());
        coders.put(ListValue.class, new ListCoder());
        coders.put(DictionaryValue.class, new DictionaryCoder());
    }

    @Override
    public AValue<?> decode(StringReader reader) throws IOException, CodingException {
        for (ACoder coder : coders.values()) {
            reader.mark(Integer.MAX_VALUE);
            if (coder.canDecode(reader)) {
                reader.reset();
                return coder.decode(reader);
            } else {
                reader.reset();
            }
        }
        throw new CodingException("Invalid bencoded string.");
    }

    @Override
    public void encode(AValue<?> value, StringBuilder sb) throws CodingException {
        ACoder coder = coders.get(value.getClass());
        if (coder == null) {
            throw new CodingException("Unsupported value: " + value.getClass().getName());
        }
        coder.encode(value, sb);
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
