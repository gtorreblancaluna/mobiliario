/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author gerardo torreblanca luna
 * handle exception in layer dao
 */
public class DataOriginException extends Exception{
    
    private transient Throwable cause;
    private transient String message;

    public DataOriginException() {
        super();
    }

    public DataOriginException(final String message) {
        this.message = message;
    }

    public DataOriginException(final String message, final Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
    

