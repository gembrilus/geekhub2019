package iv.nakonechnyi.specialnumbers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model : ViewModel(){

    val data: MutableLiveData<MutableList<Long>> = MutableLiveData()

    init {
        data.value = mutableListOf()
    }

    fun add(n: Long) {
        data.value?.add(n)
        data.value = data.value
    }

    fun clear() {
        data.value = mutableListOf()
    }


    override fun onCleared() {
        super.onCleared()
        clear()
    }
}