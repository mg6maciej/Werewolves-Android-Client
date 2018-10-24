package pl.mg6.werewolves

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.join_activity.*

class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_activity)
        join_join.setOnClickListener { onJoinClick() }
        if (savedInstanceState == null) {
            join_name.setText(getMyName())
        }
    }

    private fun onJoinClick() {
        val name = join_name.text.toString()
        setMyName(name)
        api.join(JoinGameInfo(
                joinId = join_code.text.toString(),
                id = getMyId(),
                name = join_name.text.toString()
        ))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onJoinSuccess, this::onJoinError)
    }

    private fun onJoinSuccess(info: GameInfo) {
        startActivity(Intent(this, GameActivity::class.java)
                .putExtra("id", info.id))
        finish()
    }

    private fun onJoinError(error: Throwable) {
        Log.e("TAG", "error", error)
    }
}
