package kr.mcv.bagil.skhcsApi

import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.util.concurrent.Callable

class PostRunnable(
    private val plugin: JavaPlugin
) : Runnable {

    private val config = plugin.config
    private val server = plugin.server

    override fun run() {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(
            URI(
                String.format(
                    "https://api.skhcs.com/v2/get_command_skript/?port=%s", config.getString("port")
                )
            )
        ).header("Content-Type", "application/x-www-form-urlencoded").POST(
            BodyPublishers.ofString(
                String.format(
                    "key=%s& loginid=%s", config.getString("key"), config.getString("login-id")
                )
            )
        ).build()
        val response = client.send(request, BodyHandlers.ofString(Charsets.UTF_8))

        when {
            response.body().contains("data_required") -> {
                server.consoleSender.sendMessage("§6§l[SKHCS] §c입력된 API 키와 아이디가 불일치 합니다.")
                return
            }

            response.body().contains("wrong_data") -> {
                server.consoleSender.sendMessage("§6§l[SKHCS] §cAPI 키, 아이디, 포트를 다시 확인해주세요.")
                return
            }

            response.body().contains("ip_denied") -> {
                server.consoleSender.sendMessage("§6§l[SKHCS] §c사이트에 아이피 포트를 입력해주세요.")
                return
            }
        }

        val body = Json.decodeFromString<Body>(response.body())
        val commands = body.commands.map { it.split(" ").drop(1).joinToString(" ") }.filterNot { it.isBlank() }
        commands.forEach {
            plugin.server.scheduler.callSyncMethod(
                plugin,
                Callable { server.dispatchCommand(server.consoleSender, it) })
        }
        if (config.getBoolean("debug")) {
            server.consoleSender.sendMessage(String.format("§6§l[SKHCS] §e%s개의 명령어가 처리되었습니다.", commands.size))
        }
    }
}