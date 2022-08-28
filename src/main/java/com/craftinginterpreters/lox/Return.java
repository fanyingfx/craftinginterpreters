package com.craftinginterpreters.lox;

/**
 * @author fan
 * 8/28/22
 */
public class Return extends RuntimeException {
    final Object value;
    public Return(Object value) {
        super(null,null,false,false);
        this.value=value;
    }
}
