package com.example.myphotoview.activity.fragment

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myphotoview.R
import com.example.myphotoview.adapter.GalleryAdapter
import com.example.myphotoview.data.NetWorkStatus
import com.example.myphotoview.viewmodel.GalleryViewModel
import kotlinx.android.synthetic.main.gallery_fragment.*


class GalleryFragment : Fragment() {
    private val galleryViewModel by viewModels<GalleryViewModel>()
    companion object {
        fun newInstance() =
            GalleryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.gallery_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter: GalleryAdapter =
            GalleryAdapter(galleryViewModel)
        recyclerView.apply {
            this.adapter=galleryAdapter
            layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

        galleryViewModel.photoListPageData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)
        })

        galleryViewModel.netWorkStatus.observe(viewLifecycleOwner, Observer {
            galleryAdapter.updateNetWorkStatus(it)
            swipeView.isRefreshing=it== NetWorkStatus.INITIAL_LOADING
        })

        swipeView.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuRefresh ->{
                swipeView?.isRefreshing=true
                Handler().postDelayed({galleryViewModel.resetQuery()},1000)
            }
            R.id.tryMenu ->{
                galleryViewModel.retry()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
