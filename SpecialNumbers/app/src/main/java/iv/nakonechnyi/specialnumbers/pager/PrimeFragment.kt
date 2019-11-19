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
import java.lang.IllegalArgumentException

class PrimeFragment : Factory() {
    override val NAME: String
        get() = "Prime"//getString(R.string.title_prime)
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var task: Thread? = null

    private var isTaskNotStopped = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.input.setOnEditorActionListener { _, _, _ ->

            clearAll()

            val number: Long
            try {
                number = view.input.text.toString().toLong()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, getString(R.string.input_correct_number), Toast.LENGTH_LONG).show()
                return@setOnEditorActionListener false
            }

            isTaskNotStopped = true
            task = Thread {
                getPrimes(number) { num ->
                    if(isTaskNotStopped) handler.post {
                        model.add(num)
                    }
                }
            }.apply {
                isDaemon = true
                setUncaughtExceptionHandler { thread, throwable ->
                    thread.interrupt()
                    throwable.printStackTrace()
                }
            }.also {
                it.start()

            }

            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.clear -> {
                clearAll()
                fragmentView.input.text.clear()
                true
            }
            R.id.stop -> {
                isTaskNotStopped = false
                task?.interrupt()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun calculate(n: Long) = longArrayOf()
}