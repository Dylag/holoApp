import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import com.warnyul.android.widget.FastVideoView

class VideoThread(_videoView: FastVideoView, _context:Context, _name:String): Thread() {
    var videoView: FastVideoView
    private var uri:Uri? = null
    private var mediaPlayer = MediaPlayer()

    var context:Context? = null


    init {
        videoView = _videoView
        videoView.surfaceTextureListener = object : SurfaceTextureListener{
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                mediaPlayer.setSurface(Surface(surface))
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {return true}

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
        }

        context = _context
        name = _name

    }

    override fun run() {
        mediaPlayer.start()
        Log.d("maid", "#")


    }

    fun prepare()
    {
        mediaPlayer.prepare()
        Log.d("maid", "$name prepared")
    }

    fun setUri(_uri:Uri)
    {
        uri = _uri
        //Log.d("maid", "$name got uri: $uri")
        mediaPlayer.setDataSource(context!!, uri!!)
        //Log.d("maid", "$name data source: $context and $_uri")
    }

}