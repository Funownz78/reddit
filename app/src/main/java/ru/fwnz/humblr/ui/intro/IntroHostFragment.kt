package ru.fwnz.humblr.ui.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentIntro2Binding
import ru.fwnz.humblr.databinding.FragmentIntroHostBinding
import kotlin.math.min

class IntroHostFragment : Fragment() {
    private var _binding: FragmentIntroHostBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragments: List<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroHostBinding.inflate(inflater, container, false)
        fragments = listOf(
            Intro0Fragment().apply {
               nextItem = {
                   binding.viewPager2.currentItem += 1
               }
            },
            Intro1Fragment().apply {
                nextItem = {
                    binding.viewPager2.currentItem += 1
                }
            },
            Intro2Fragment().apply {
                nextItem = {
                    val action = IntroHostFragmentDirections.actionIntroHostFragmentToLoginFragment()
                    findNavController().navigate(action)
                }
            },
        )
        binding.viewPager2.adapter = IntroAdapter(requireActivity(), fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { _, _ ->
        }.attach()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}