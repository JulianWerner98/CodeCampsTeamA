package de.uniks.ws2122.cc.teamA

object Constant {
    //MSG
    val ERROR_MSG = "There are some Errors! Try again."
    val LOGIN_SUCCESS_MSG = "Login successful!"
    val REGISTER_SUCCESS_MSG = "Register successful"
    val NEW_PASSWORD_SUCCESS_MSG = "Mail sent successfully"
    val NICKNAME_ERROR = "Nickname is already taken. Choose another"
    val ALREADY_INGAME_ERROR = "You are already in Game"
    //Links
    val FIREBASE_URL = "https://codecampsteama-default-rtdb.europe-west1.firebasedatabase.app"
    //Path
    const val USERS_PATH = "Users"
    const val FRIENDS_PATH = "Friends"
    const val FRIEND_REQUEST_PATH = "Friendrequest"
    const val SEND_PATH = "send"
    const val RECEIVED_PATH = "received"

    //TikTakToe Path
    const val TTTQ = "TicTacToeQ"
    const val GAMES = "Games"
    const val TTT = "TicTacToe"
    const val TTTFIELD = "Field"
    const val LASTTURN = "LastTurn"
    const val INGAME = "InGame"
    const val OPENMATCHES = "OpenMatches"
    const val PLAYER1 = "Player1"
    const val PLAYER2 = "Player2"
    const val NICKNAME = "nickname"
    const val WINNER = "Winner"
    const val ID = "Id"
    const val DRAW = "Draw"
    const val BLANKFIELD = "_________"

    //Invite Path
    const val MATCH_REQUEST = "MatchRequest"
    const val FROM = "From"
    const val GAME = "Game"

    //Sport Challenge
    const val SPORT_CHALLENGE = "SportChallenge"
    const val MODE = "Mode"
    const val OPTION = "Option"
    const val KEY = "Key"
    const val STEPS = "Steps"
    const val METERS = "Meters"
    const val TIME = "Time"

    //average step of a german
    // deutsche Durchschnittsgröße ist ~173cm. durchschnittliche Schrittlänge bei 170cm ist ~70cm
    const val AVERAGE_STEP = 0.7f
}