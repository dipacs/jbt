/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode;

import com.eagerlogic.bencode.coders.ACoder;
import com.eagerlogic.bencode.coders.CodingException;
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
    }

    @Override
    public AValue<?> decode(StringReader reader) throws IOException, CodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encode(AValue<?> value, StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
