package iv.nakonechnyi.specialnumbers.pager

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import iv.nakonechnyi.specialnumbers.*
import iv.nakonechnyi.specialnumbers.R
import iv.nakonechnyi.specialnumbers.workers.CalcWorker
import iv.nakonechnyi.specialnumbers.workers.fibonacci
import iv.nakonechnyi.specialnumbers.workers.getArmstrongs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_layout.view.input
import kotlinx.android.synthetic.main.fragment_layout.view.*
import java.lang.IllegalArgumentException

internal const val NUMBER = "NUMBER"
internal const val SAVED_NUMBER = "SAVED_NUMBER"
internal const val FRAGMENT_POSITION = "FRAGMENT_POS"
internal const val OUTPUT_DATA_KEY = "OUTPUT"
private const val WORKER_TAG = "CALC_WORKER"
private const val IS_NOT_STOPPED = "IS_NOT_STOPPED"

abstract class Factory : Fragment() {

    private val workManager by lazy { context?.let { WorkManager.getInstance(it) } }
    protected val model by lazy { ViewModelProviders.of(this).get(Model::class.java) }

    private lateinit var fragmentView: View
    private lateinit var mAdapter: PrimeAdapter
    private lateinit var mRecyclerView: RecyclerView
    protected var number: Long? = null
    protected var isTaskNotStopped = false

    abstract val NAME: String
    abstract fun calculate(n: Long): LongArray

    companion object {

        fun creator(index: Int): Factory = when (index) {
            0 -> PrimeFragment()
            1 -> ArmstrongFragment()
            2 -> FibonacciFragment()
            else -> throw IllegalArgumentException()
        }

        fun count() = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (savedInstanceState != null) {
            number = savedInstanceState.getLong(SAVED_NUMBER)
            isTaskNotStopped = savedInstanceState.getBoolean(IS_NOT_STOPPED)
        }

        if (isTaskNotStopped) {
            eval()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        number?.let { outState.putLong(SAVED_NUMBER, it) }
        outState.putBoolean(IS_NOT_STOPPED, isTaskNotStopped)
    }

    override fun onPause() {
        super.onPause()
        cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_layout, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecyclerView()

        model.data.observe(this, Observer { _ ->
            model.data.value?.let { data ->
                if (data.isNotEmpty()) {
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.clear -> {
                clearAll()
                true
            }
            R.id.stop -> {

                val text = fragmentView.input.text.toString()
                if (text.isNotEmpty()){
                    number = text.toLong()
                }

                if (number != null) {
                    isTaskNotStopped = !isTaskNotStopped

                    if (isTaskNotStopped) {
                        item.setIcon(R.drawable.ic_stop_black_24dp)
                        eval()
                    } else {
                        item.setIcon(R.drawable.ic_play_arrow_black_24dp)
                        cancel()
                    }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.input_correct_number),
                        Toast.LENGTH_LONG
                    ).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setupRecyclerView() {
        mAdapter = PrimeAdapter(model)
        mRecyclerView = fragmentView.recycler_view.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        }
    }

    private fun run(number: Long): WorkRequest {
        val input = Data.Builder()
            .putLong(NUMBER, number)
            .putInt(FRAGMENT_POSITION, (activity as MainActivity).pager.currentItem)
            .build()
        val op = OneTimeWorkRequest.Builder(CalcWorker::class.java)
            .addTag(WORKER_TAG)
            .setConstraints(Constraints.NONE)
            .setInputData(input)
            .build()
        workManager?.enqueue(op)
        return op
    }

    private fun showResult(op: WorkRequest) {
        workManager?.getWorkInfoByIdLiveData(op.id)
            ?.observe(this@Factory, Observer { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val array = workInfo.outputData.getLongArray(OUTPUT_DATA_KEY)
                        array?.let { model.set(it) }
                        isTaskNotStopped = false
                }
            })
    }

    protected open fun eval() {
        val op = number?.let { run(it) }
        op?.let { showResult(it) }
    }

    protected open fun cancel() {
        workManager?.cancelAllWorkByTag(WORKER_TAG)
    }

    protected fun clearAll() {
        model.clear()
        mAdapter.notifyDataSetChanged()
        number = null
        fragmentView.input.text.clear()
        isTaskNotStopped = false
    }
}

class ArmstrongFragment : Factory() {
    override val NAME = "Armstrong"
    override fun calculate(n: Long) = getArmstrongs(n)
}

class FibonacciFragment : Factory() {
    override val NAME = "Fibonacci"
    override fun calculate(n: Long) = fibonacci(n)
}
