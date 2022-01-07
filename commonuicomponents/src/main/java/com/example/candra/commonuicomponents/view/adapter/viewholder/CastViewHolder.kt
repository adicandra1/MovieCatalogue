package com.example.candra.commonuicomponents.view.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.candra.commonuicomponents.R
import com.example.candra.data.api.TheMovieDBApi
import com.example.candra.data.model.Cast
import com.example.candra.utils.Constant.CAST_IMAGE_SIZE

class CastViewHolder(
    view: View,
    private val glide: RequestManager,
    private val listener: (Cast) -> Unit = {} //listener can be null
) :
    BaseViewHolder<Cast>(view) {
    private val castPhoto: ImageView = view.findViewById(R.id.castPhoto)
    private val nameAndRoleField: TextView = view.findViewById(R.id.nameAndRole)

    override fun bindItem(item: Cast) {
        val nameAndRoleText = "${item.castName} as ${item.castRole}"
        nameAndRoleField.text = nameAndRoleText
        glide.load(item.castPhoto?.let { TheMovieDBApi.getPosterImage(CAST_IMAGE_SIZE, it) })
            .apply(RequestOptions().error(R.drawable.error))
            .into(castPhoto)

        itemView.setOnClickListener {
            listener(item)
        }
    }
}