package com.myproject.nexa.cqrs;

/**
 * Interface for command handlers
 * @param <C> The command type
 * @param <R> The return type
 */
public interface CommandHandler<C extends Command, R> {
    R handle(C command);
}