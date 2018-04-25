package com.github.suhininalex.codefinder.preprocessing

import com.google.gson.*
import java.lang.reflect.Type

object TokenSerializer: JsonSerializer<Token>, JsonDeserializer<Token> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Token = with(context) {
        val type = json.asJsonObject["type"].asString
        return when (type) {
            "LiteralToken" -> deserialize(json, LiteralToken::class.java)
            "TypeToken" -> deserialize(json, TypeToken::class.java)
            "IdentifierToken" -> deserialize(json, IdentifierToken::class.java)
            "CallToken" -> deserialize(json, CallToken::class.java)
            "CommentToken" -> deserialize(json, CommentToken::class.java)
            else -> throw IllegalArgumentException("There is no registered token class for type $type")
        }
    }

    override fun serialize(src: Token, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src).asJsonObject.apply {
            addProperty("type", src.javaClass.simpleName)
        }
    }
}

val TokenJsonSerializer: Gson = GsonBuilder().registerTypeAdapter(Token::class.java, TokenSerializer).create()