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

class PrimeFragment : Factory() {
    override val NAME: String
        get() = "Prime"//getString(R.string.title_prime)
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var lastNumber: Long = 0L
    private var task: Thread? = null
    private var isTaskNotStopped = false

    private val LAST_NUMBER_KEY = "LAST_NUMBER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null){
            lastNumber = savedInstanceState.getLong(LAST_NUMBER_KEY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.input.setOnEditorActionListener { _, _, _ ->

            clearAll()

            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(LAST_NUMBER_KEY, lastNumber)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.clear -> {
                clearAll()
                fragmentView.input.text.clear()
                true
            }
            R.id.stop -> {

                var number: Long? = null
                try {
                    number = fragmentView.input.text.toString().toLong()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(context, getString(R.string.input_correct_number), Toast.LENGTH_LONG).show()
                }

                number?.let {
                    isTaskNotStopped = !isTaskNotStopped
                    if (isTaskNotStopped) {
                        item.setIcon(R.drawable.ic_stop_black_24dp)
                        task = startTask(number)
                        task?.start()
                    } else {
                        item.setIcon(R.drawable.ic_play_arrow_black_24dp)
                        task?.interrupt()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun startTask(number: Long): Thread {
        return Thread {
            getPrimes(number, lastNumber, model.data.value!!) { num ->
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException){}
                handler.post{
                    lastNumber = num + 1
                    model.update()
                }
            }
        }.apply {
            isDaemon = true
            setUncaughtExceptionHandler { thread, throwable ->
                thread.interrupt()
                throwable.printStackTrace()
            }
        }
    }

    override fun calculate(n: Long) = longArrayOf()
}