package com.jeremy.android.consumer.rxbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by Jeremy on 2017/3/16.
 * 基于RxJava2
 */
public class RxBus {

    private static volatile RxBus mDefaultInstance;

    private final FlowableProcessor<Object> _bus = PublishProcessor.create().toSerialized();

    private final Map<Class<?>, Object> mStickyEventMap = new ConcurrentHashMap<>();

    private RxBus() {

    }

    public static RxBus get() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    /**
     * 判断是否有订阅者
     *
     * @return
     */
    public boolean hasSubscribers() {
        return _bus.hasSubscribers();
    }

    /**
     * 重置RxBus
     */
    public void reset() {
        mDefaultInstance = null;
    }

    /**
     * 发送一个新的事件
     *
     * @param event
     * @return
     */
    public RxBus post(Object event) {
        _bus.onNext(event);
        return this;
    }

    /**
     * 根据传递的eventType类型返回特定类型(eventType)的被观察者
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        return _bus.ofType(eventType);
    }

    /**
     * 发送一个新的粘性事件
     *
     * @param event
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * 根据传递的eventType类型返回特定类型（eventType）的被观察者
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toFlowableSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Flowable<T> flowable = _bus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);
            if (event != null) {
                return flowable.mergeWith(Flowable.create(e -> e.onNext(eventType.cast(event)), BackpressureStrategy.DROP));
            } else {
                return flowable;
            }
        }
    }

    /**
     * 根据eventType获取Stick事件
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }
}
