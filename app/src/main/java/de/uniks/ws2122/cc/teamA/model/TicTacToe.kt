package de.uniks.ws2122.cc.teamA.model

class TicTacToe :Game {
    private var phase: String = ""
    private var fields: ArrayList<Field> = ArrayList()

    constructor(phase:String){
        this.phase = phase
    }

    fun withFields(field: Field) {
        if (fields.contains(field)) return
        fields.add(field)
        field.setGame(this)
    }

    fun withoutField(field: Field) {
        if (!fields.contains(field)) return
        field.setGame(null)
        fields.remove(field)
    }

}