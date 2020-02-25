package com.cyberveda.client.models.chat


data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}