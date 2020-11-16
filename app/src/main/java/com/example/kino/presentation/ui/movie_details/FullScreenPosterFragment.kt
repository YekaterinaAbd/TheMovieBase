package com.example.kino.presentation.ui.movie_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.kino.R
import com.example.kino.data.network.ORIGINAL_IMAGE_URL
import com.example.kino.presentation.utils.constants.POSTER_INTENT_KEY
import com.squareup.picasso.Picasso

class FullScreenPosterFragment : Fragment() {

    private var posterPath: String? = null
    private lateinit var image: ImageView
    private lateinit var ivBack: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_screen_poster, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        if (bundle?.get(POSTER_INTENT_KEY) != null) {
            posterPath = bundle.get(POSTER_INTENT_KEY) as String?
        }
        bindViews(view)
    }

    private fun bindViews(view: View) {
        image = view.findViewById(R.id.full_screen_image)

        if (!posterPath.isNullOrEmpty()) {
            Picasso.get().load(ORIGINAL_IMAGE_URL + posterPath).into(image)
        }

        ivBack = view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        view.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}