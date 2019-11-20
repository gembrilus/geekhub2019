package iv.nakonechnyi.specialnumbers.pager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import iv.nakonechnyi.specialnumbers.workers.getPrimes

private const val LAST_NUMBER_KEY = "LAST_NUMBER"

class PrimeFragment : Factory() {

    override val name = "Prime"
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

                    kotlin.runCatching { Thread.sleep(100) }

                    handler.post {
                        lastNumber = num + 1
                        model.add(num)
                    }
                    true
                } else false
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