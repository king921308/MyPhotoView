package com.example.myphotoview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val factory =PixabayDataSourceFactory(application)
    val photoListPageData =factory.toLiveData(1)
    val netWorkStatus =Transformations.switchMap(factory.pixabayDataSource) {it.netWorkStatus}
    fun resetQuery(){
        photoListPageData.value?.dataSource?.invalidate()
    }

    fun retry(){
        factory.pixabayDataSource.value?.retry?.invoke()
    }
}
