package com.mateuszb.justlisten.ui.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.mateuszb.justlisten.R
import com.mateuszb.justlisten.data.models.Song

import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item.view.tvPrimary
import kotlinx.android.synthetic.main.swipe_item.view.*
import javax.inject.Inject

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {


    override val differ = AsyncListDiffer(this, diffCallback)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val songText = "${song.title} - ${song.author}"
            tvPrimary.text = songText

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}