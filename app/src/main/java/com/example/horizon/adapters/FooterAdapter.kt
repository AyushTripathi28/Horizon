package com.example.horizon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.horizon.R
import com.example.horizon.databinding.FooterLoadingBinding

class FooterAdapter : LoadStateAdapter<FooterAdapter.LoadingViewHolder>() {

    class LoadingViewHolder(val viewBinding: FooterLoadingBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        if (loadState == LoadState.Loading){
            holder.viewBinding.pbFooter.visibility = View.VISIBLE
        }else{
            holder.viewBinding.pbFooter.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.footer_loading, parent, false)
        val viewBinding = FooterLoadingBinding.bind(view)
        return LoadingViewHolder(viewBinding)
    }
}