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
internal const val FRAGMENT_POSITION = "FRAGMENT_POS"
internal const val OUTPUT_DATA_KEY = "OUTPUT"
private const val WORKER_TAG = "CALC_WORKER"

abstract class Factory : Fragment() {

    private val workManager by lazy { context?.let { WorkManager.getInstance(it) } }
    protected val model by lazy { ViewModelProviders.of(this).get(Model::class.java) }

    protected lateinit var fragmentView: View
    protected lateinit var mAdapter: PrimeAdapter
    private lateinit var mRecyclerView: RecyclerView

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

        setUpRecyclerView()

        model.data.observe(this, Observer { list ->
                mAdapter.update(model.data.value!!)
        })

        view.input.setOnEditorActionListener { _, _, _ ->
            val number: Long
            try {
                number = view.input.text.toString().toLong()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, getString(R.string.input_correct_number), Toast.LENGTH_LONG).show()
                return@setOnEditorActionListener false
            }

            val op = run(number)
            showResult(op)
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.clear -> {
                model.clear()
                mAdapter.clear()
                fragmentView.input.text.clear()
                true
            }
            R.id.stop -> {
                workManager?.cancelAllWorkByTag(WORKER_TAG)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setUpRecyclerView() {
        mAdapter = PrimeAdapter()
        mRecyclerView = fragmentView.recycler_view.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        }
    }

    private fun run(number: Long): WorkRequest {
        model.clear()
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
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val array = workInfo.outputData.getLongArray(OUTPUT_DATA_KEY)
                        array?.forEach { model.add(it) }
                    }
                    WorkInfo.State.FAILED -> {
                        Toast.makeText(context, getString(R.string.eval_failed), Toast.LENGTH_LONG).show()
                    }
                    WorkInfo.State.CANCELLED -> {
                        Toast.makeText(context, getString(R.string.task_stopped), Toast.LENGTH_LONG).show()
                    }
                    WorkInfo.State.RUNNING -> {}
                    else -> { workManager?.enqueue(op) }
                }
            })
    }
}

class ArmstrongFragment : Factory() {
    override val NAME: String
        get() = "Armstrong"//getString(R.string.title_armstrong)

    override fun calculate(n: Long): LongArray {
        return getArmstrongs(n)
    }
}

class FibonacciFragment : Factory() {
    override val NAME: String
        get() = "Fibonacci"//getString(R.string.title_fibonacci)

    override fun calculate(n: Long): LongArray {
        return fibonacci(n)
    }
}
