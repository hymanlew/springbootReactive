package com.hyman.springboot.config;

import org.reactivestreams.Subscription;

public class OperationSubscriber<T> implements org.reactivestreams.Subscriber<T> {

    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("s = " + s);
        s.request(1);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Failed");
    }

    @Override
    public void onComplete() {
        System.out.println("Completed");
    }

    @Override
    public void onNext(T o) {
        System.out.println("Inserted");
    }
}
