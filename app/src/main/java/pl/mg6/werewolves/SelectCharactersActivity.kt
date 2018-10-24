package pl.mg6.werewolves

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.select_characters_activity.*
import pl.mg6.werewolves.Character.*

class SelectCharactersActivity : AppCompatActivity() {

    private val characters by lazy {
        listOf(
                select_characters_doppelganger to Doppelganger,
                select_characters_werewolf_1 to Werewolf,
                select_characters_werewolf_2 to Werewolf,
                select_characters_minion to Minion,
                select_characters_seer to Seer,
                select_characters_robber to Robber,
                select_characters_witch to Witch,
                select_characters_troublemaker to Troublemaker,
                select_characters_insomniac to Insomniac,
                select_characters_tanner to Tanner
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_characters_activity)
        characters.forEach {
            it.first.setOnCheckedChangeListener { _, _ -> updateInfo() }
        }
        updateInfo()
        select_characters_start.setOnClickListener { createGame() }
        if (savedInstanceState == null) {
            select_characters_name.setText(getMyName())
        }
    }

    private fun updateInfo() {
        val count = characters.count { it.first.isChecked }
        val enough = count >= 6
        select_characters_start.text = if (enough) {
            "Start for ${count - 3} players"
        } else {
            "Need ${6 - count} more characters"
        }
        select_characters_start.isEnabled = enough
    }

    private fun createGame() {
        val name = select_characters_name.text.toString()
        setMyName(name)
        api.create(CreateGameInfo(
                characters = characters.filter { it.first.isChecked }.map { it.second },
                gameTimeInMinutes = select_characters_time.text.toString().toLong(),
                id = getMyId(),
                name = name
        ))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCreateSuccess, this::onCreateError)
    }

    private fun onCreateSuccess(info: GameInfo) {
        startActivity(Intent(this, WaitForPlayersActivity::class.java)
                .putExtra("join_id", info.joinId!!)
                .putExtra("id", info.id))
    }

    private fun onCreateError(error: Throwable) {
        Log.e("TAG", "error", error)
    }
}
