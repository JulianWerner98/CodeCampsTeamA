package de.uniks.ws2122.cc.teamA.model

class Player {
    private var userID: String = ""
    private var nickname: String = ""
    private var fields: ArrayList<Field> = ArrayList()
    private var game: Game? = null

    constructor(userId: String, nickname: String) {
        this.userID = userId
        this.nickname = nickname
    }

    fun withFields(field: Field) {
        if (fields.contains(field)) return
        fields.add(field)
        field.setPlayer(this)
    }

    fun withoutField(field: Field) {
        if (!fields.contains(field)) return
        field.setPlayer(null)
        fields.remove(field)
    }

    fun setGame(game: Game) {
        this.game = game
        game!!.withPlayer(this)
    }
}