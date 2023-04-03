package com.hangman.android.core.storageadapter.internal

import androidx.room.*
import com.hangman.android.bouding.*

/**
 * Describes the representation of a Word into the Room Database.
 *
 * id will be the Primary key and won't be automatically generated.
 * date should be unique as only one Word per day should be playable.
 */
@Entity(
    tableName="word_of_the_day_table",
    indices = [Index(value = ["date"], unique = true)]
)
data class RoomWord(
    @PrimaryKey
    val id: String,
    val date: Long,
    val word: String
) {
    /**
     * Builds a Room representation of a Word from an IWord instance
     *
     * @param word The IWord instance from which to build our RoomWord
     */
    constructor(word: IWord): this(
        id = word.id,
        date = word.date,
        word = word.word
    )

    /**
     * Transforms this RoomWord into an IWord instance for using elsewhere in the app.
     *
     * @return The newly created IWord instance.
     */
    fun asModel(): IWord = Word(
        id = id,
        date = date,
        word = word
    )
}

/**
 * Describes the representation of a Letter into the Room Database.
 *
 * id will be the Primary key and will be automatically generated.
 *
 * a pair (letter, goodLetter) should be unique as it represents a single possibility of player's
 * action. The associated table will/should be pre-populated from an already generated database
 * and should not change during the life of the application.
 */
@Entity(
    tableName="played_letters_table",
    indices = [Index(value = ["letter", "goodLetter"], unique = true)]
)
data class RoomLetter(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val letter: Char,
    val goodLetter: Boolean
) {
    /**
     * Transforms this RoomLetter into an ILetter instance for using elsewhere in the app.
     *
     * @return The newly created ILetter instance.
     */
    fun asModel(): ILetter = Letter(
        letter = letter,
        goodLetter = goodLetter
    )
}

/**
 * Describes the basic representation of a game detail into the Room Database.
 * It will not represent a full game detail and will not be fully populated as, for exemple, played
 * letters will not be included. This is why it cannot directly be turned into a model instance
 * (IGameDetail)
 *
 * id will be the Primary key and will be automatically generated.
 * The wordId Foreign Key represents the one to one relationship between a game and the played Word.
 */
@Entity(
    tableName="game_details_table",
    foreignKeys = [
        ForeignKey(
            entity = RoomWord::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomGameDetail(
     @PrimaryKey(autoGenerate = true)
     val id: Int,
     val date: Long,
     val played: Boolean,
     val result: Boolean,
     val gamePlayTime: Long,
     val wordId: String
) {
    /**
     * Builds the Room representation of the basic GameDetail from an IGameDetail instance
     *
     * @param gameDetail The IGameDetail instance from which to build our RoomGameDetail
     */
    constructor(gameDetail: IGameDetail): this(
        id = gameDetail.id,
        date = gameDetail.date,
        played = gameDetail.played,
        result = gameDetail.result,
        gamePlayTime = gameDetail.gamePlayTime,
        wordId = gameDetail.wordOfTheDay.id
    )
}

/**
 * This table represents the many to many relationship between a game detail and the played letters.
 * As played letters are unique and predefined in the database, this many to many relationship is
 * necessary.
 *
 * The pair of relationship ids (game detail id, letter id) will be used as primary key, as only one
 * such pair can exist for a specific set of values.
 * The foreign keys will represent the relationship between an entry in this table and an entry
 * in each associated tables (gameDetail and letter).
 * The order field is here to keep the information of the order in which the letters were played in
 * a specific game.
 */
@Entity(
    tableName = "game_details_letters_cross_ref",
    primaryKeys = ["gameDetailsId", "letterId"],
    foreignKeys = [
        ForeignKey(
            entity = RoomGameDetail::class,
            parentColumns = ["id"],
            childColumns = ["gameDetailsId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomLetter::class,
            parentColumns = ["id"],
            childColumns = ["letterId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameDetailsLettersCrossRef(
    val gameDetailsId: Int,
    val letterId: Int,
    val order: Int,
)

/**
 * Describes the representation of a game history item into the Room Database.
 *
 * This is a compound class and is not directly represented in the database. Instead, informations
 * will be extracted from different tables, namely, the GameDetail table, and the Word table.
 */
data class RoomGameHistoryItem(
    // A Game History Item embeds a full RoomGameDetail, every field from the RoomGameDetail entity
    // will be available in a RoomGameHistoryItem instance.
    @Embedded
    val gameDetail: RoomGameDetail,

    // We only need the actual word, not the id nor the date, for an history item. So we will only
    // extract this information from the Word table.
    @Relation(
        parentColumn = "wordId",
        entityColumn = "id",
        entity = RoomWord::class,
        projection = ["word"]
    )
    val word: String
) {
    /**
     * Transforms this RoomGameHistoryItem into an IGameHistoryItem instance for using elsewhere in
     * the app.
     *
     * @return The newly created IGameHistoryItem instance.
     */
    fun asModel(): IGameHistoryItem = GameHistoryItem(
        id = gameDetail.id,
        date = gameDetail.date,
        wordId = gameDetail.wordId,
        word = word,
        result = gameDetail.result,
        played = gameDetail.played
    )
}

/**
 * Described a fully populated GameDetail as found in the Room Database.
 *
 * This is a compound class, and is not directly represented in the database. Instead, informations
 * will be extracted from needed tables.
 * As-is, an instance of this class can be autogenerated by the Room API directly, from the data in
 * needed tables. But in that case, some additional steps might not be performed, such as ordering
 * the letters in the order they were played. To add those steps, the instantiation of this class
 * should be made manually with data extracted from the database beforehand.
 */
data class PopulatedRoomGameDetail(
    // A Game History Item embeds a full RoomGameDetail, every field from the RoomGameDetail entity
    // will be available in a RoomGameHistoryItem instance.
    @Embedded
    val gameDetail: RoomGameDetail,

    // The game history will also embed the full associated RoomWord instance, and all Word data
    // will be accessible from the "word" member
    @Relation(
        parentColumn = "wordId",
        entityColumn = "id",
        entity = RoomWord::class
    )
    val word: RoomWord,

    // The game played letters will be embedded as well. They are accessible via the junction table
    // that describes the many to many relationship between a game and the played letters.
    // By default the Room API will fetch them in the order they are retrieved from the junction
    // table. ie : pretty much randomly.
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GameDetailsLettersCrossRef::class,
            parentColumn = "gameDetailsId",
            entityColumn = "letterId"
        )
    )
    val playedLetters: List<RoomLetter>
) {
    /**
     * Transforms this PopulatedRoomGameGameDetail into an IGameDetail instance for using elsewhere
     * in the app.
     *
     * @return The newly created IGameDetail instance.
     */
    fun asModel(): IGameDetail = GameDetail(
        id = gameDetail.id,
        date = gameDetail.date,
        wordOfTheDay = word.asModel(),
        result = gameDetail.result,
        played = gameDetail.played,
        playedLetters = playedLetters.map {it.asModel()},
        gamePlayTime = gameDetail.gamePlayTime
    )
}
