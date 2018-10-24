package pl.mg6.werewolves

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

class InfoForPlayerAdapter : JsonDeserializer<InfoForPlayer> {

    override fun deserialize(json: JsonElement, typeOfT: java.lang.reflect.Type, context: JsonDeserializationContext): InfoForPlayer {
        val type = json.asJsonObject.get("type").asString
        val typePascalCase = type.split("_").joinToString(separator = "", transform = String::capitalize)
        val className = "pl.mg6.werewolves.${typePascalCase}Info"
        return context.deserialize(json, Class.forName(className))
    }
}
