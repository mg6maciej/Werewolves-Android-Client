package pl.mg6.werewolves

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.launcher_activity.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)
        launcher_version.text = "Version: ${packageManager.getPackageInfo(packageName, 0).versionName}"
    }

    override fun onResume() {
        super.onResume()
        api.games(getMyId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMyGames) { enableButtons() }
    }

    private fun showMyGames(games: List<GameInfoPlayersOnly>) {
        enableButtons()
        launcher_games.layoutManager = LinearLayoutManager(this)
        launcher_games.adapter = GamesAdapter(games)
    }

    private fun enableButtons() {
        launcher_create.isEnabled = true
        launcher_create.setOnClickListener { onCreateClick() }
        launcher_join.isEnabled = true
        launcher_join.setOnClickListener { onJoinClick() }
    }

    private fun onCreateClick() {
        startActivity(Intent(this, SelectCharactersActivity::class.java))
    }

    private fun onJoinClick() {
        startActivity(Intent(this, JoinActivity::class.java))
    }
}
