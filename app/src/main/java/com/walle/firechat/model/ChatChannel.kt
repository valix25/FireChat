package com.walle.firechat.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor(): this(mutableListOf())
}