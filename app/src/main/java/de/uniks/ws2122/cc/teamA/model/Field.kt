package de.uniks.ws2122.cc.teamA.model

class Field() {
    private var game: Game? = null
    private var player: Player? = null
    private var left: Field? = null
    private var right: Field? = null
    private var bottom: Field? = null
    private var top: Field? = null


    public fun setPlayer(playerIn: Player?) {
        if (player != null) playerIn!!.withoutField(this)
        player = playerIn
        player!!.withFields(this)
    }

    fun setLeft(field: Field?) {
        if (left == field) return
        right = field
        field!!.setRight(this)
    }

    private fun setRight(field: Field?) {
        if (right == field) return
        right = field
        field!!.setLeft(this)
    }

    fun setTop(field: Field?) {
        if (top == field) return
        top = field
        field!!.setBottom(this)
    }

    private fun setBottom(field: Field?) {
        if (bottom == field) return
        bottom = field
        field!!.setTop(this)
    }

    fun setGame(game: Game?) {
        if (this.game == game) return
        this.game = game
        if (game is TicTacToe) (game as TicTacToe).withFields(this)
    }
}