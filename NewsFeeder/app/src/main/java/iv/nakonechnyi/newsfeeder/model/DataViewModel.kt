package iv.nakonechnyi.newsfeeder.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {
    private val data = MutableLiveData<List<Article>>()

    fun getData(): LiveData<List<Article>> = data

    fun setData(list: List<Article>) {
        data.value = list
    }
}
