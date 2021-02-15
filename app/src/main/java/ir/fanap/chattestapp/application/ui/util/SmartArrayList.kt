package ir.fanap.chattestapp.application.ui.util

import rx.subjects.BehaviorSubject

class SmartArrayList<T> : ArrayList<T>() {


    override fun add(element: T): Boolean {


        onInsertObserver.onNext(element)

        return super.add(element)


    }






    val onInsertObserver: BehaviorSubject<T> = BehaviorSubject.create()



}