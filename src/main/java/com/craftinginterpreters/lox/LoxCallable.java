package com.craftinginterpreters.lox;

import java.util.List;

/**
 * @author fan
 * 8/28/22
 */
public interface LoxCallable {
    Object call(Interpreter interpreter, List<Object> arguments);

    int arity();
}
