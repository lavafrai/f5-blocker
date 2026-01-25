package ru.lavafrai.svogame.f5blocker.config

import ru.lavafrai.svogame.f5blocker.F5BlockerMod

object F5ClientState {
    private var f5Blocked: Boolean = true

    fun isF5Blocked(): Boolean = f5Blocked
    fun isF5Allowed(): Boolean = !f5Blocked

    fun setF5Blocked(blocked: Boolean) {
        F5BlockerMod.LOGGER.info("[F5Blocker] Client received F5 blocked state: $blocked")
        f5Blocked = blocked
    }
}