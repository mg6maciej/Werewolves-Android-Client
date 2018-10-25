package pl.mg6.werewolves

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.game_activity.*

class GameActivity : AppCompatActivity() {

    private val handler by lazy { Handler() }
    private val callForGameRunnable = object : Runnable {
        override fun run() {
            callForGame()
            handler.postDelayed(this, 3000)
        }
    }

    private var readyToVote = false

    private fun callForGame() {
        api.game(intent.getStringExtra("id"), getMyId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess(info: GameInfo) {
        val characters = info.characters.sorted()
        game_characters.text = "Roles: ${characters.dropLast(1).joinToString()} and ${characters.last()}"
        game_names_container.removeAllViews()
        info.names.forEachIndexed { index, it ->
            game_names_container.addView(TextView(this).apply {
                text = "${index + 1}. $it"
                if (info.result != null) {
                    if (index in info.result.winners) {
                        append(" won!")
                    }
                    append("\n${info.result.starting[index]} -> ${info.result.ending[index]}"
                            + "\nVoted for: ${info.names[info.result.votes[index]]}")
                }
            })
        }
        if (info.full) {
            game_info.text = info.yourPrivateInfo.joinToString(separator = "\n") {
                when (it) {
                    is StartingRoleInfo -> "Your initial role is ${it.character}." + when (it.character) {
                        Character.Doppelganger -> "\nYou can be anything! Select player to become what they are."
                        Character.Werewolf -> ""
                        Character.Minion -> ""
                        Character.Seer -> "\nYou may look at any player's card or two of the center cards."
                        Character.Robber -> "\nYou may steal a card from any other player."
                        Character.Witch -> "\nYou may turn someone into new a role."
                        Character.Troublemaker -> "\nYou may switch cards between two other players."
                        Character.Insomniac -> ""
                        Character.Tanner -> "\nYou don't like your job and would like everybody to vote on you."
                    }
                    is DoppelgangerInfo -> "You are now ${it.character}."
                    is WerewolfInfo -> when (it.otherWerewolves.size) {
                        0 -> "You are a lone werewolf. You may look at one of the center cards."
                        1 -> "Player \"${info.names[it.otherWerewolves[0]]}\" is also a werewolf."
                        else -> "Players ${it.otherWerewolves.dropLast(1).joinToString { "\"${info.names[it]}\"" }} and \"${info.names[it.otherWerewolves.last()]}\" are also werewolves.\n"
                    }
                    is LoneWerewolfInfo -> "You saw ${it.character} in the middle. Try pretending you are not a werewolf."
                    is MinionInfo -> when (it.werewolves.size) {
                        0 -> "There are no werewolves. Make other think someone is a werewolf to win."
                        1 -> "Player \"${info.names[it.werewolves[0]]}\" is a werewolf. Protect him."
                        else -> "Players ${it.werewolves.dropLast(1).joinToString() { "\"${info.names[it]}\"" }} and ${info.names[it.werewolves.last()]} are werewolves. Protect them all."
                    }
                    is SeerCenterInfo -> "You saw ${it.leftCharacter} and ${it.rightCharacter}."
                    is SeerPlayerInfo -> "You saw ${it.character}."
                    is RobberInfo -> "Your new role is ${it.character}."
                    is WitchInfo -> "You selected ${it.character}. Who will it be?"
                    is InsomniacInfo -> "Your role at the end of all the switcheroos is ${it.character}."
                    else -> "Unknown: ${it.javaClass.simpleName}"
                }
            }
            game_actions_container.removeAllViews()
            info.yourAllowedActions.forEach {
                game_actions_container.addView(Button(this).apply {
                    text = if (it.players.isNotEmpty()) {
                        it.players.joinToString(separator = " & ") { info.names[it] }
                    } else if (it.center.isNotEmpty()) {
                        it.center.joinToString(separator = " & ") {
                            when (it) {
                                0 -> "LEFT"
                                1 -> "MIDDLE"
                                2 -> "RIGHT"
                                else -> throw IllegalStateException()
                            }
                        }
                    } else {
                        "Skip"
                    }
                    setOnClickListener { _ -> onActionClick(it) }
                })
            }
            if (!readyToVote && info.dayStarted && !info.timeOver) {
                game_info.append("\n\nEverybody performed their actions. Time to talk!")
                if (!readyToVote) {
                    game_actions_container.addView(Button(this).apply {
                        text = "Ready to vote"
                        setOnClickListener { readyToVote = true }
                    })
                }
            }
            if ((readyToVote || info.timeOver) && info.result == null) {
                game_info.append("\n\nTime to vote!")
                info.names.withIndex().filter { it.index != info.yourNumber }.forEach {
                    game_actions_container.addView(Button(this).apply {
                        text = it.value
                        setOnClickListener { _ -> onVoteClick(it.index) }
                    })
                }
                if (!readyToVote) {
                    (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(200)
                }
            }
        } else {
            game_info.text = "Waiting for other players."
        }
    }

    private fun onActionClick(action: Action) {
        api.action(intent.getStringExtra("id"), getMyId(), action)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onVoteClick(player: Int) {
        api.vote(intent.getStringExtra("id"), getMyId(), Vote(player))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onError(error: Throwable) {
        Log.e("TAG", "error", error)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        handler.post(callForGameRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(callForGameRunnable)
    }
}
