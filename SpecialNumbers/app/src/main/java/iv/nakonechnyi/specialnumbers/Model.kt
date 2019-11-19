package iv.nakonechnyi.specialnumbers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model : ViewModel(){

    val data: MutableLiveData<MutableList<Long>> = MutableLiveData()

    init {
        data.value = mutableListOf()
    }

    fun set(list: LongArray) {
        data.value = list.toMutableList()
    }

    fun add(n: Long){
        data.value?.add(n)
        val list = data.value
        data.value = list
    }

    fun clear() {
        data.value = mutableListOf()
    }


    fun update() {
        data.value = data.value
    }


/*    override fun onCleared() {
        super.onCleared()
        clear()
    }*/
}