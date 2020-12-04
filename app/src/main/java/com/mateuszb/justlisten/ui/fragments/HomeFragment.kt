package com.mateuszb.justlisten.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.mateuszb.justlisten.R
import com.mateuszb.justlisten.other.Resource
import com.mateuszb.justlisten.ui.adapters.SongsAdapter
import com.mateuszb.justlisten.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var songsAdapter: SongsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()

        songsAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it)

        }
    }

    private fun setupRecyclerView() = rvAllSongs.apply {
        adapter = songsAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }


    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Success -> {
                    allSongsProgressBar.isVisible = false
                    result.data?.let {
                        songsAdapter.songs = it
                    }
                }
                is Resource.Error -> Unit

                is Resource.Loading -> {
                    allSongsProgressBar.isVisible = true
                }

            }
        }
    }
}