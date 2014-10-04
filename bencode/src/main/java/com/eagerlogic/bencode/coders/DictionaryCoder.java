/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

import com.eagerlogic.bencode.AValue;
import com.eagerlogic.bencode.BEncode;
import com.eagerlogic.bencode.DictionaryValue;
import com.eagerlogic.bencode.StringValue;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 *
 * @author dipacs
 */
public class DictionaryCoder extends ACoder<DictionaryValue> {

    @Override
    public DictionaryValue decode(StringReader reader) throws IOException, CodingException {
        reader.read();
        DictionaryValue res = new DictionaryValue();
        while (true) {
            reader.mark(Integer.MAX_VALUE);
            if (reader.read() == 'e') {
                break;
            }
            reader.reset();
            String key = ((StringValue)BEncode.getInstance().decode(reader)).getValue();
            res.getValue().put(key, BEncode.getInstance().decode(reader));
        }
        return res;
    }

    @Override
    public void encode(DictionaryValue value, StringBuilder sb) {
        sb.append("d");
        for (Map.Entry<String, AValue<?>> entry : value.getValue().entrySet()) {
            BEncode.getInstance().encode(new StringValue(entry.getKey()), sb);
            BEncode.getInstance().encode(entry.getValue(), sb);
        }
        sb.append("e");
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        return reader.read() == 'd';
    }
    
}
