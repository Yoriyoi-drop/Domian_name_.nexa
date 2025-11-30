package com.myproject.nexa.cqrs;

/**
 * Interface for query handlers
 * @param <Q> The query type
 * @param <R> The return type
 */
public interface QueryHandler<Q extends Query, R> {
    R handle(Q query);
}