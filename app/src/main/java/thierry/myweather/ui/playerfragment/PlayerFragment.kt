package thierry.myweather.ui.playerfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import thierry.myweather.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {
    private lateinit var simplePlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlayerBinding.inflate(layoutInflater)
        val rootView = binding.root

        simplePlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = simplePlayer
        val mediaItem1 = MediaItem.fromUri("https://i.imgur.com/7bMqysJ.mp4")
        val mediaItem2 = MediaItem.fromUri("https://i.imgur.com/jKjet6a.mp4")
        val mediaItem3 = MediaItem.fromUri("https://i.imgur.com/rn8FCg7.mp4")
        simplePlayer.addMediaItem(mediaItem1)
        simplePlayer.addMediaItem(mediaItem2)
        simplePlayer.addMediaItem(mediaItem3)
        simplePlayer.prepare()
        simplePlayer.play()

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        simplePlayer.release()
    }

    companion object {
        fun newInstance() = PlayerFragment()
    }

}