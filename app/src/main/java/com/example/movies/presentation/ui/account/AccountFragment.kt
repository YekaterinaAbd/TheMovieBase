package com.example.movies.presentation.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.changeFragment
import com.example.movies.data.model.account.Account
import com.example.movies.domain.model.MoviesType
import com.example.movies.presentation.ui.lists.movies.MoviesFragment
import com.example.movies.presentation.ui.sign_in.SignInActivity
import com.example.movies.presentation.utils.constants.MOVIE_TYPE
import com.google.android.material.appbar.AppBarLayout
import de.hdodenhof.circleimageview.CircleImageView
import org.koin.android.ext.android.inject
import kotlin.math.abs


class AccountFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {

    companion object {
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
        private const val ALPHA_ANIMATIONS_DURATION: Long = 200
        private const val PERCENTAGE_TO_ANIMATE_AVATAR = 20
    }

    private var isTheTitleContainerVisible = true
    private var isAvatarShown = true
    private var mMaxScrollSize: Int = 0

    private lateinit var titleContainer: LinearLayout
    private lateinit var username: TextView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var viewFavourites: TextView
    private lateinit var viewWatchList: TextView
    private lateinit var viewRated: TextView
    private lateinit var logOutButton: ImageView
    private lateinit var profileImage: CircleImageView

    private val accountViewModel: AccountViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        observeData()
    }

    private fun bindViews(view: View) = with(view) {

        toolbar = findViewById(R.id.main_toolbar)
        username = findViewById(R.id.username)
        titleContainer = findViewById(R.id.main_linearlayout_title)
        appBarLayout = findViewById(R.id.main_appbar)
        profileImage = findViewById(R.id.profile_image)
        logOutButton = view.findViewById(R.id.btnLogOut)
        viewFavourites = view.findViewById(R.id.btnFavourites)
        viewWatchList = view.findViewById(R.id.watchlist)
        viewRated = view.findViewById(R.id.btnRated)

        mMaxScrollSize = appBarLayout.totalScrollRange
        appBarLayout.addOnOffsetChangedListener(this@AccountFragment)

        viewFavourites.setOnClickListener {
            openListFragment(MoviesType.FAVOURITES)
        }

        viewWatchList.setOnClickListener {
            openListFragment(MoviesType.WATCH_LIST)
        }

        viewRated.setOnClickListener {
            openListFragment(MoviesType.RATED)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun openListFragment(type: MoviesType) {
        changeFragment<MoviesFragment>(
            container = R.id.framenav,
            bundle = bundleOf(MOVIE_TYPE to type),
            animation = NavigationAnimation.SLIDE_LEFT
        )
    }

    private fun observeData() {
        accountViewModel.liveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is AccountViewModel.State.AccountResult -> {
                    setUserData(result.data)
                }
                is AccountViewModel.State.AccountLocalResult -> {
                    username.text = result.username
                }
                is AccountViewModel.State.LogOutSuccessful -> {
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    requireActivity().finish()
                }
                is AccountViewModel.State.LogOutFailed -> {
                    Toast.makeText(requireContext(), "Log out failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setUserData(account: Account) {
        username.text = account.name
//        if (account.avatar?.gravatar != null) {
//            Picasso.get()
//                .load(GRAVATAR_URL + account.avatar.gravatar + ".jpg")
//                .into(profileImage)
//        }
    }

    private fun logOut() {
        accountViewModel.logOut()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, offset: Int) {
        val maxScroll = appBarLayout!!.totalScrollRange
        val percentage = abs(offset).toFloat() / maxScroll.toFloat()

        handleAlphaOnTitle(percentage)
        handleImageVisibility(appBarLayout, offset)
    }

    private fun handleImageVisibility(appBarLayout: AppBarLayout?, offset: Int) {
        if (mMaxScrollSize == 0) mMaxScrollSize = appBarLayout!!.totalScrollRange

        val percentage: Int = abs(offset) * 100 / mMaxScrollSize

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && isAvatarShown) {
            isAvatarShown = false
            profileImage.animate()
                .scaleY(0f).scaleX(0f)
                .setDuration(ALPHA_ANIMATIONS_DURATION)
                .start()
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !isAvatarShown) {
            isAvatarShown = true
            profileImage.animate()
                .scaleY(1f).scaleX(1f)
                .start()
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (isTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, View.INVISIBLE)
                isTheTitleContainerVisible = false
            }
        } else {
            if (!isTheTitleContainerVisible) {
                startAlphaAnimation(titleContainer, View.VISIBLE)
                isTheTitleContainerVisible = true
            }
        }
    }

    private fun startAlphaAnimation(v: View, visibility: Int) {
        val alphaAnimation =
            if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
        alphaAnimation.duration = ALPHA_ANIMATIONS_DURATION
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }
}
