package iv.nakonechnyi.newsfeeder.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel(val data: MutableLiveData<List<Article>> = MutableLiveData()) : ViewModel()
