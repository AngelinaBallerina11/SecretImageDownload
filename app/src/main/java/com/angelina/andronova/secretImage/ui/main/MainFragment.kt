package com.angelina.andronova.secretImage.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.angelina.andronova.secretImage.App
import com.angelina.andronova.secretImage.R
import com.angelina.andronova.secretImage.databinding.MainFragmentBinding
import com.angelina.andronova.secretImage.di.modules.ViewModelFactory
import com.angelina.andronova.secretImage.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        App.component.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        binding.vm = viewModel

        val preferenceRepository = (requireActivity().application as App).preferenceRepository

        preferenceRepository.isDarkThemeLive.observe(this, Observer { isDarkTheme ->
            isDarkTheme?.let { binding.swDarkTheme.isChecked = it }
        })

        binding.swDarkTheme.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }

        viewModel.screenState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ScreenState.Idle -> {
                    popProgress()
                }
                ScreenState.Loading -> {
                    pushProgress()
                    activity?.hideKeyboard()
                }
                is ScreenState.Error -> {
                    popProgress()
                    activity?.hideKeyboard()
                    viewModel.setIdleState()
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.imageDownloadResult.observe(viewLifecycleOwner, Observer { bitmap ->
            if (bitmap != null) {
                binding.ivPlaceholder.setImageBitmap(bitmap)
            } else {
                binding.ivPlaceholder.setImageResource(R.drawable.ic_image_placeholder)
            }
        })
    }

    private fun pushProgress() {
        with(binding) {
            pbProgress.visibility = View.VISIBLE
            ivPlaceholder.visibility = View.INVISIBLE
        }
    }

    private fun popProgress() {
        with(binding) {
            pbProgress.visibility = View.INVISIBLE
            ivPlaceholder.visibility = View.VISIBLE
        }
    }
}
