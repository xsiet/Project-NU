package io.github.xsiet.projectnu.utils

import com.google.gson.Gson
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

data class PlayerSkinData(
    val id: String,
    val name: String,
    val properties: List<Property>,
    val profileActions: Map<*, *>
)
fun GameProfile.setSkin(skinUUID: UUID) {
    val url = URL("https://sessionserver.mojang.com/session/minecraft/profile/${skinUUID}?unsigned=false")
    var line: String?
    val response = StringBuilder()
    (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        BufferedReader(InputStreamReader(inputStream)).apply {
            while (readLine().also { line = it } != null) { response.append(line) }
            close()
        }
        disconnect()
    }
    this.properties.apply {
        removeAll("textures")
        put("textures", Gson().fromJson(response.toString(), PlayerSkinData::class.java).properties[0])
    }
}