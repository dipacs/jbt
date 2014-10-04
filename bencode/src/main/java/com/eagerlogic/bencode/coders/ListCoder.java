/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

import com.eagerlogic.bencode.AValue;
import com.eagerlogic.bencode.BEncode;
import com.eagerlogic.bencode.ListValue;
import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @author dipacs
 */
public class ListCoder extends ACoder<ListValue> {

    @Override
    public ListValue decode(StringReader reader) throws IOException, CodingException {
        reader.read();
        ListValue res = new ListValue();
        while (true) {
            reader.mark(Integer.MAX_VALUE);
            if (reader.read() == 'e') {
                break;
            }
            reader.reset();
            res.getValue().add(BEncode.getInstance().decode(reader));
        }
        return res;
    }

    @Override
    public void encode(ListValue value, StringBuilder sb) {
        sb.append('l');
        for (AValue<?> item : value.getValue()) {
            BEncode.getInstance().encode(item, sb);
        }
        sb.append('e');
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        return reader.read() == 'l';
    }
    
}
