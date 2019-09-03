package ir.fanap.chattestapp.application.ui.util

import rx.subjects.BehaviorSubject

class SmartHashMap<K, V> : HashMap<K, V>() {


    override fun put(key: K, value: V): V? {

        onInsertObserver.onNext(Pair(first = key, second = value))

        return super.put(key, value)


    }

    val onInsertObserver: BehaviorSubject<Pair<K, V>> = BehaviorSubject.create()




}