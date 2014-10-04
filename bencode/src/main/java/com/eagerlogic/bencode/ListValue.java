/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dipacs
 */
public class ListValue extends AValue<List<AValue>> {

    public ListValue() {
        super(new ArrayList<AValue>());
    }

    public ListValue(List<AValue> value) {
        super(value);
    }
    
}
