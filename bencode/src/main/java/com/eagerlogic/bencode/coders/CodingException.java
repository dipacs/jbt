/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.bencode.coders;

/**
 *
 * @author dipacs
 */
public class CodingException extends Exception {

    public CodingException(String message) {
        super(message);
    }

    public CodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodingException(Throwable cause) {
        super(cause);
    }
    
}
