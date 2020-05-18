package com.example.myphotoview

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


class PixabayDataSourceFactory(private val context: Context): DataSource.Factory<Int,PhotoItem>() {
    private val _pixabayDataSource=MutableLiveData<PixabayDataSource>()
    var pixabayDataSource : LiveData<PixabayDataSource> =_pixabayDataSource
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also { _pixabayDataSource.postValue(it) }
    }
}