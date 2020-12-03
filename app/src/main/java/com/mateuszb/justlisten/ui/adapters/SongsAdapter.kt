package com.mateuszb.justlisten.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mateuszb.justlisten.R
import com.mateuszb.justlisten.data.models.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongsAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaID == newItem.mediaID
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onItemClickListener: ((Song) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.author
            glide.load(song.imageURL).into(ivItemImage)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }
}