package ru.fwnz.humblr.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentIntro2Binding

class Intro2Fragment : Fragment() {
    private var _binding: FragmentIntro2Binding? = null
    private val binding get() = _binding!!
    var nextItem: ()->Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntro2Binding.inflate(inflater, container, false)
        binding.header.next.text = getString(R.string.done)
        binding.header.next.setOnClickListener { nextItem() }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}