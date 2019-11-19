package iv.nakonechnyi.specialnumbers.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import iv.nakonechnyi.specialnumbers.pager.FRAGMENT_POSITION
import iv.nakonechnyi.specialnumbers.pager.Factory
import iv.nakonechnyi.specialnumbers.pager.NUMBER
import iv.nakonechnyi.specialnumbers.pager.OUTPUT_DATA_KEY

class CalcWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val N = inputData.getLong(NUMBER, 0L)
        val pos = inputData.getInt(FRAGMENT_POSITION, 0)
        val fragment =
            Factory.creator(pos)
        return try {
            val result = fragment.calculate(N)
            val output = Data.Builder()
                .putLongArray(OUTPUT_DATA_KEY, result)
                .build()
            Result.success(output)
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}