/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

import com.eagerlogic.bencode.IntegerValue;
import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @author dipacs
 */
public class IntegerCoder extends ACoder<IntegerValue> {

    @Override
    public IntegerValue decode(StringReader reader) throws IOException, CodingException {
        reader.read();
        String str = "";
        while (true) {
            char c = (char) reader.read();
            if (c == 'e') {
                break;
            }
            str += c;
        }
        return new IntegerValue(Integer.parseInt(str));
    }

    @Override
    public void encode(IntegerValue value, StringBuilder sb) {
        sb.append("i").append(value.getValue().intValue()).append("e");
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        return reader.read() == 'i';
    }
    
}
