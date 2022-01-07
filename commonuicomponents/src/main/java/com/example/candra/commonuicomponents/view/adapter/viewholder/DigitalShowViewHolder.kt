package com.example.candra.commonuicomponents.view.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.candra.commonuicomponents.R
import com.example.candra.commonuicomponents.view.printNoticeIfNull
import com.example.candra.data.api.TheMovieDBApi
import com.example.candra.data.model.DigitalShow
import com.example.candra.utils.Constant.POSTER_IMAGE_SIZE

class DigitalShowViewHolder(
    private val view: View,
    private val glide: RequestManager,
    private val listener: (DigitalShow) -> Unit
) : BaseViewHolder<DigitalShow>(view) {
    private val movieTitleField: TextView = view.findViewById(R.id.itemTitleField)
    private val movieDescriptionField: TextView = view.findViewById(R.id.itemDescriptionField)
    private val moviePosterField: ImageView = view.findViewById(R.id.itemPosterFIeld)

    override fun bindItem(item: DigitalShow) {
        movieTitleField.text = item.title
        movieDescriptionField.text = item.overview?.printNoticeIfNull(view.context)
        glide.load(item.poster?.let {
            TheMovieDBApi.getPosterImage(
                POSTER_IMAGE_SIZE,
                it
            )
        }).into(moviePosterField)

        itemView.setOnClickListener {
            listener(item)
        }
    }

}