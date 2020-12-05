package com.mateuszb.justlisten.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.mateuszb.justlisten.R
import com.mateuszb.justlisten.data.models.Song
import com.mateuszb.justlisten.exoplayer.isPlaying
import com.mateuszb.justlisten.exoplayer.toSong
import com.mateuszb.justlisten.other.Resource
import com.mateuszb.justlisten.ui.adapters.SwipeSongAdapter
import com.mateuszb.justlisten.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var currentPlayingSong: Song? = null

    private var playbakcState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToObservers()

        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbakcState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    currentPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        ivPlayPause.setOnClickListener{
            currentPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if(newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            currentPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it.let { result ->
                when(result) {
                    is Resource.Success -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if(songs.isNotEmpty()) {
                                glide.load((currentPlayingSong ?: songs[0].imageURL)).into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
                        }
                    }

                    is Resource.Error -> Unit

                    is Resource.Loading -> Unit
                }
            }
        }

        mainViewModel.currentPlayingSong.observe(this) {
            if(it == null) return@observe

            currentPlayingSong = it.toSong()
            glide.load(currentPlayingSong?.imageURL).into(ivCurSongImage)
            switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
        }

        mainViewModel.playbackState.observe(this) {
            playbakcState = it
            ivPlayPause.setImageResource(
                if(playbakcState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when(result){
                    is Resource.Error -> {
                        Snackbar.make(rootLayout, result.errorMessage ?: "An unknown error occurred", Snackbar.LENGTH_LONG).show()
                    }
                    else -> {Unit}
                }
            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when(result){
                    is Resource.Error -> {
                        Snackbar.make(rootLayout, result.errorMessage ?: "An unknown error occurred", Snackbar.LENGTH_LONG).show()
                    }
                    else -> {Unit}
                }
            }
        }
    }
}