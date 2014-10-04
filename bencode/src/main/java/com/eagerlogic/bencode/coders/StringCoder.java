/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

import com.eagerlogic.bencode.StringValue;
import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @author dipacs
 */
public class StringCoder extends ACoder<StringValue> {

    @Override
    public StringValue decode(StringReader reader) throws IOException, CodingException {
        String countStr = "";
        while (true) {
            char c = (char) reader.read();
            if (c == ':') {
                break;
            }
            if (c < '0' || c > '9') {
                throw new CodingException("Invalid character found.");
            }
        }
        
        int count = Integer.parseInt(countStr);
        char[] chars = new char[count];
        int readed = 0;
        int actReaded;
        while ((actReaded = reader.read(chars, readed, count - readed)) > 0) {
            readed += actReaded;
        }
        
        return new StringValue(new String(chars));
    }

    @Override
    public void encode(StringValue value, StringBuilder sb) {
        sb.append(value.getValue().length()).append(":").append(value.getValue());
    }

    @Override
    public boolean canDecode(StringReader reader) throws IOException {
        char c = (char) reader.read();
        return c >= '0' && c <= '9';
    }
    
}
