package pl.mg6.werewolves

import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WerewolvesApi {

    @GET("games")
    fun games(@Query("playerId") playerId: String): Single<List<GameInfoPlayersOnly>>

    @POST("games")
    fun create(@Body info: CreateGameInfo): Single<GameInfo>

    @PUT("games")
    fun join(@Body info: JoinGameInfo): Single<GameInfo>

    @GET("games/{id}")
    fun game(@Path("id") id: String, @Query("playerId") playerId: String): Single<GameInfo>

    @POST("games/{id}/actions")
    fun action(@Path("id") id: String, @Query("playerId") playerId: String, @Body action: Action): Single<GameInfo>

    @POST("games/{id}/votes")
    fun vote(@Path("id") id: String, @Query("playerId") playerId: String, @Body vote: Vote): Single<GameInfo>
}

val api: WerewolvesApi by lazy {
    Retrofit.Builder()
            .baseUrl("https://werewolves-online.herokuapp.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                    .registerTypeAdapter(InfoForPlayer::class.java, InfoForPlayerAdapter())
                    .create()))
            .build()
            .create(WerewolvesApi::class.java)
}

enum class Character {
    Doppelganger,
    Werewolf,
    Minion,
    Seer,
    Robber,
    Witch,
    Troublemaker,
    Insomniac,
    Tanner,
}

data class Vote(
        val player: Int
)

data class Action(val players: List<Int> = emptyList(), val center: List<Int> = emptyList())

data class CreateGameInfo(
        val characters: List<Character>,
        val gameTimeInMinutes: Long,
        val id: String,
        val name: String
)

data class JoinGameInfo(
        val joinId: String,
        val id: String,
        val name: String
)

data class GameInfo(
        val joinId: String?,
        val id: String,
        val characters: List<Character>,
        val names: List<String>,
        val yourNumber: Int,
        val full: Boolean,
        val dayStarted: Boolean,
        val timeOver: Boolean,
        val yourPrivateInfo: List<InfoForPlayer>,
        val yourAllowedActions: List<Action>,
        val result: Result?
)

data class GameInfoPlayersOnly(
        val id: String,
        val names: List<String>
)

interface InfoForPlayer

data class StartingRoleInfo(val character: Character) : InfoForPlayer

data class DoppelgangerInfo(val character: Character) : InfoForPlayer

data class WerewolfInfo(val otherWerewolves: List<Int>) : InfoForPlayer

data class LoneWerewolfInfo(val character: Character) : InfoForPlayer

data class MinionInfo(val werewolves: List<Int>) : InfoForPlayer

data class SeerPlayerInfo(val character: Character) : InfoForPlayer

data class SeerCenterInfo(val leftCharacter: Character, val rightCharacter: Character) : InfoForPlayer

data class RobberInfo(val character: Character) : InfoForPlayer

data class WitchInfo(val character: Character) : InfoForPlayer

data class InsomniacInfo(val character: Character) : InfoForPlayer

data class Result(
        val winners: List<Int>,
        val votes: List<Int>,
        val starting: List<Character>,
        val ending: List<Character>
)
