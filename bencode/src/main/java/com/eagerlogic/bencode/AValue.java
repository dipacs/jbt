/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode;

/**
 *
 * @author dipacs
 */
public abstract class AValue<T> {
    
    private final T value;

    public AValue(T value) {
        if (value == null) {
            throw new NullPointerException("The 'value' parameter can not be null.");
        }
        this.value = value;
    }

    public T getValue() {
        return value;
    }
    
}
