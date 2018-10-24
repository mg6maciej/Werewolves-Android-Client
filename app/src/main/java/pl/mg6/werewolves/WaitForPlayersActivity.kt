package pl.mg6.werewolves

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.wait_for_players_activity.*

class WaitForPlayersActivity : AppCompatActivity() {

    private val handler by lazy { Handler() }
    private val callForGameRunnable = object : Runnable {
        override fun run() {
            callForGame()
            handler.postDelayed(this, 3000)
        }
    }

    private fun callForGame() {
        api.game(intent.getStringExtra("id"), getMyId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess(info: GameInfo) {
        if (info.full) {
            startGame()
        }
    }

    private fun onError(error: Throwable) {
        Log.e("TAG", "error", error)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wait_for_players_activity)
        wait_for_players_code.text = intent.getStringExtra("join_id")
        wait_for_players_start.setOnClickListener { startGame() }
        handler.post(callForGameRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(callForGameRunnable)
    }

    private fun startGame() {
        startActivity(Intent(this, GameActivity::class.java)
                .putExtra("id", intent.getStringExtra("id")))
        finish()
    }
}
