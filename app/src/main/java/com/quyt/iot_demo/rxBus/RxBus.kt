package com.quyt.iot_demo.rxBus

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.function.Consumer

object RxBus {
    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any){
        publisher.onNext(event)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun<T> listen(eventType: Class<T>, onNextConsumer: Consumer<T>): Disposable{
        return publisher.ofType(eventType).subscribe({
            onNextConsumer.accept(it)
        }, {
            Log.e("RxBus", "message: " + it.localizedMessage)
        })
    }
}