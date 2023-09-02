package ru.fwnz.humblr.ui.main.profile.profile

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModelFactory = App.daggerComponent.getProfileViewModelFactory()
    private val viewModel: ProfileViewModel by viewModels { profileViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.progressCircular.visibility = View.GONE

        binding.clearSavedButton.setOnClickListener {
            viewModel.unSaveComments()
        }

        binding.logoutButton.setOnClickListener {
            val appAuthComponent = App.daggerComponent.getAppAuthComponent()
            val listener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val endSessionIntent = appAuthComponent.getLogoutRequest()
                        startActivity(endSessionIntent)
                    }
                    else -> {}
                }
            }
            val dialog = AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setTitle(R.string.logout_alert_title)
                .setMessage(R.string.logout_alert_message)
                .setPositiveButton(R.string.action_yes, listener)
                .setNegativeButton(R.string.action_no, listener)
                .create()

            dialog.show()
        }

        binding.followSubbreditFromProfileButton.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationNotificationsToFriendsFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.me.collect {
                    it?.let {
                        binding.apply {
                            avatarProgress.visibility = View.VISIBLE
                            Glide.with(requireContext())
                                .load(it.avatarUrl)
                                .listener(object : RequestListener<Drawable>{
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        avatarProgress.visibility = View.GONE
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        avatarProgress.visibility = View.GONE
                                        return false
6                                    }
                                })
                                .centerCrop()
                                .error(R.drawable.logo)
                                .into(avatarImage)
                            textName.text = it.name
                            textLink.text = it.link
                            val unixTime = it.accountCreated // example Unix timestamp
//                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            val date = Date(unixTime * 1000)
                            val dateString = dateFormat.format(date)
                            textKarma.text = getString(
                                R.string.account_details,
                                it.karma,
                                dateString
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loadingState.collectLatest { loadStates ->
                    val sourceIsRefresh = loadStates == ProfileViewModel.LoadingState.Loading
                    binding.progressCircular.visibility =
                        if (sourceIsRefresh) View.VISIBLE
                        else View.GONE
                }
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.profile).uppercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}