package com.example.candra.commonuicomponents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.candra.commonuicomponents.R
import com.example.candra.commonuicomponents.view.adapter.viewholder.BaseViewHolder
import com.example.candra.commonuicomponents.view.adapter.viewholder.CastViewHolder
import com.example.candra.data.model.Cast
import com.example.candra.data.model.DigitalShow
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.commonuicomponents.view.adapter.viewholder.DigitalShowViewHolder
import com.example.candra.commonuicomponents.view.adapter.viewholder.FavoriteShowViewHolder
import com.example.candra.utils.Constant

class ItemListAdapter(
    private val glide: RequestManager,
    private var data: MutableList<*> = mutableListOf<Any>(),
    private val listener: ((Any) -> Unit) = {} //listener can be null
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    fun setData(newData: MutableList<*>) {
        data = newData
        notifyDataSetChanged()
    }

    private fun inflateLayout(parent: ViewGroup, layoutResource: Int): View =
        LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)

    private fun determineViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            Constant.TYPE_DIGITALSHOW -> {
                val view = inflateLayout(parent, layoutResource = R.layout.item_layout)
                DigitalShowViewHolder(view, glide, listener)
            }
            Constant.TYPE_FAVORITESHOW -> {
                val view = inflateLayout(parent, layoutResource = R.layout.item_layout)
                FavoriteShowViewHolder(view, glide, listener)
            }
            Constant.TYPE_CAST -> {
                val view = inflateLayout(parent, layoutResource = R.layout.cast_item_layout)
                CastViewHolder(view, glide)
            }
            else -> throw IllegalArgumentException("Invalid View Type: $viewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return determineViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when (holder) {
            is DigitalShowViewHolder -> holder.bindItem(element as DigitalShow)
            is FavoriteShowViewHolder -> holder.bindItem(element as DigitalShowDetails)
            is CastViewHolder -> holder.bindItem(element as Cast)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DigitalShow -> Constant.TYPE_DIGITALSHOW
            is DigitalShowDetails -> Constant.TYPE_FAVORITESHOW
            is Cast -> Constant.TYPE_CAST
            else -> throw IllegalArgumentException("Invalid type data at $position")
        }
    }
}