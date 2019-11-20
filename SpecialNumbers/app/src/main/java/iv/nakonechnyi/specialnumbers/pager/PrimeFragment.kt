package iv.nakonechnyi.specialnumbers.pager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import iv.nakonechnyi.specialnumbers.R
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
                        lastNumber = num + 2
                        model.add(num)
                        if(lastNumber >= it-2) {
                            mMenu.findItem(R.id.stop).setIcon(R.drawable.ic_play_arrow_black_24dp)
                            mMenu.findItem(R.id.clear).isEnabled = true
                        }
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

    override fun clearAll() {
        super.clearAll()
        lastNumber = 0L
    }

    override fun cancel() {
        task?.interrupt()
        task = null
    }
}