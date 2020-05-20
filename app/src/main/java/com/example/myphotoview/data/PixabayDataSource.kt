package com.example.myphotoview.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.myphotoview.bean.PhotoItem
import com.example.myphotoview.bean.Pixabay
import com.example.myphotoview.netutils.VolleySinglerton
import com.google.gson.Gson

enum class NetWorkStatus {
    INITIAL_LOADING,
    LOADING,
    LOADED,
    FAILED,
    COMPLETED
}

class PixabayDataSource(private val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {
    var retry: (() -> Any)? = null
    private val _networkStatus = MutableLiveData<NetWorkStatus>()
    val netWorkStatus: LiveData<NetWorkStatus> = _networkStatus
    private val keyWrods = arrayOf("girl", "sport", "car", "cat", "dog", "cool").random()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        retry = null
        _networkStatus.postValue(NetWorkStatus.INITIAL_LOADING)
        val url =
            "https://pixabay.com/api/?key=16418604-fd9681e8ab7cd971969852327&q=${keyWrods}&per_page=50&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val hitList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(hitList, null, 2)
                _networkStatus.postValue(NetWorkStatus.LOADED)
            },
            Response.ErrorListener {
                retry = {
                    loadInitial(params, callback)
                }
                _networkStatus.postValue(NetWorkStatus.FAILED)
            }
        ).also {
            VolleySinglerton.getInstance(context).requestQueue.add(it)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        retry = null
        _networkStatus.postValue(NetWorkStatus.LOADING)
        val url =
            "https://pixabay.com/api/?key=16418604-fd9681e8ab7cd971969852327&q=${keyWrods}&per_page=50&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                val hitList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(hitList, params.key + 1)
                _networkStatus.postValue(NetWorkStatus.LOADED)
            },
            Response.ErrorListener {
                if (it.toString() == "com.android.volley.ClientError") {
                    _networkStatus.postValue(NetWorkStatus.COMPLETED)
                } else {
                    retry = {
                        loadAfter(params, callback)
                    }
                    _networkStatus.postValue(NetWorkStatus.FAILED)
                }
            }
        ).also {
            VolleySinglerton.getInstance(context).requestQueue.add(it)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
    }

}