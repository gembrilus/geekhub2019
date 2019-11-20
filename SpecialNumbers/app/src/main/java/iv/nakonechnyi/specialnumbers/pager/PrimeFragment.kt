package iv.nakonechnyi.specialnumbers.pager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import iv.nakonechnyi.specialnumbers.R
import iv.nakonechnyi.specialnumbers.workers.getPrimes
import kotlinx.android.synthetic.main.fragment_layout.view.*
import kotlinx.coroutines.delay
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

private const val LAST_NUMBER_KEY = "LAST_NUMBER"

class PrimeFragment : Factory() {

    override val NAME = "Prime"
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var lastNumber: Long = 0L
    private var task: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastNumber = savedInstanceState.getLong(LAST_NUMBER_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(LAST_NUMBER_KEY, lastNumber)
    }

    private fun startTask() = Thread {
        number?.let {
            getPrimes(it, lastNumber, model.data.value!!) { num ->
                if (isTaskNotStopped) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                    }
                    handler.post {
                        lastNumber = num + 1
                        model.add(num)
                    }
                    true
                } else {
                    false
                }
            }
        }
    }.apply {
        isDaemon = true
        setUncaughtExceptionHandler { thread, throwable ->
            thread.interrupt()
            throwable.printStackTrace()
        }
    }

    override fun calculate(n: Long) = longArrayOf()
    override fun eval() {
        task = startTask()
        task?.start()
    }

    override fun cancel() {
        task?.interrupt()
        task = null
    }
}