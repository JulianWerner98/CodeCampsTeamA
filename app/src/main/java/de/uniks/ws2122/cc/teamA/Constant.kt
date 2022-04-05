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
    val COMPASS_API_URL ="https://geoportal.kassel.de/arcgis/rest/services/Service_Daten/Freizeit_Kultur/MapServer/0/query?where=1%3D1&text=&objectIds=&time=&%20geometry=&geometryType=esriGeometryEnvelope&inSR=&spatialRel=esriSpatialRelIntersects&distance=&units=esriSRUnit_Foot&relationPar%20am=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&havingClause=&retu%20rnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVers%20ion=&historicMoment=&returnDistinctValues=false&resultOffset=&resultRecordCount=&returnExtentOnly=false&datumTransformation=&par%20ameterValues=&rangeValues=&quantizationParameters=&featureEncoding=esriDefault&f=geojson"
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

    // MentalArithmetic Path
    const val MENTALARITHMETIC = "MentalArithmetic"
    const val MENTALARITHMETICQUEUE = "MentalArithmeticQueue"
    const val MENTALARITHMETICPRIVATEQUEUE = "MentalArithmeticPrivateQueue"
    const val READY = "Ready"
    const val ARITHMETICTASKS = "arithmeticTasks"
    const val ARITHMETICANSWERS = "arithmeticAnswers"
    const val WAITINGFOROPPONENT = "Waiting for opponent"
    const val READYTOSTART = "Ready to start!"
    const val FINISHED = "finished"
    const val GAMEFINISHEDANSWERS = "gameFinishedAnswers"
    const val FINISHEDTIME = "finishedTime"
    const val MORETIME = "moreTime"
    const val LESSTIME = "lessTime"
    const val SAMETIME = "sameTime"
    const val DELETEGAME = "deleteGame"
    const val START = "Start"
    const val SURRENDER = "Surrender"
    const val PLAYERS = "Players"
    const val RESULT = "Result"

    //Compass Game
    const val COMPASS_GAME ="CompassGame"

    //Sport Challenge
    const val SPORT_CHALLENGE = "SportChallenge"
    const val MODE = "Mode"
    const val OPTION = "Option"
    const val KEY = "Key"
    const val STEPS = "Steps"
    const val METERS = "Meters"
    const val TIME = "Time"
    const val COUNTED_TIME = "CountedTime"
    const val SYSTEM_TIME = "SystemTime"

    //average step of a german
    // deutsche Durchschnittsgröße ist ~173cm. durchschnittliche Schrittlänge bei 170cm ist ~70cm
    const val AVERAGE_STEP = 0.7f
    const val MATCH_REQUEST = "matchRequest"
    const val FROM = "from"
    const val GAME = "game"
    const val INVITES = "invites"
    const val FRIENDID = "friendId"
    const val INVITEKEY = "inviteKey"
    const val MATCHTYP = "matchTyp"
    const val DEFAULT = "default"
    const val PRIVATE = "private"

    // Notifications
    const val NOTIFICATION = "Notification"
    const val NOTIFICATIONREQUEST = "NotificationRequest"
    const val CHANNEL_ID = "TeamAChannel"
    const val NOTIFICATIONARITHMETIC = "NotificationArithmetic"

    // Statistics
    const val STATISTIC = "Statistic"
    const val POINTS = "points"
    const val HISTORIE = "Historie"
    const val GAMENAME = "gamename"
    const val CURRENTUSER = "currentuser"
    const val OPPONENT = "opponent"
    const val WIN = "win"
    const val LOSE = "lose"
}