/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eagerlogic.jbt.protocol.trackerclient;

/**
 *
 * @author dipacs
 */
public class TrackerException extends Exception {

    public TrackerException(String message) {
        super(message);
    }

    public TrackerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackerException(Throwable cause) {
        super(cause);
    }
    
}
