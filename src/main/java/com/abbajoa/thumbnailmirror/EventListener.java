package com.abbajoa.thumbnailmirror;

public interface EventListener<T> {
	void onEvent(T data);
}