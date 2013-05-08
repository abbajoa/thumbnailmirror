package com.abbajoa.thumbnailmirror;

public interface Visitor<T, E extends Exception> {
	void visit(T data) throws E;
}