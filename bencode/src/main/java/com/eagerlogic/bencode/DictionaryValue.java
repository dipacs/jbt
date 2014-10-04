/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dipacs
 */
public class DictionaryValue extends AValue<Map<String, AValue<?>>> {
    
    public DictionaryValue() {
        super(new HashMap<String, AValue<?>>());
    }

    public DictionaryValue(Map<String, AValue<?>> value) {
        super(value);
    }
    
}
