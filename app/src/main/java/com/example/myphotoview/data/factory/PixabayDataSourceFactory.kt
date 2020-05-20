package com.example.myphotoview.data.factory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.myphotoview.bean.PhotoItem
import com.example.myphotoview.data.PixabayDataSource


class PixabayDataSourceFactory(private val context: Context): DataSource.Factory<Int, PhotoItem>() {
    private val _pixabayDataSource=MutableLiveData<PixabayDataSource>()
    var pixabayDataSource : LiveData<PixabayDataSource> =_pixabayDataSource
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also { _pixabayDataSource.postValue(it) }
    }
}