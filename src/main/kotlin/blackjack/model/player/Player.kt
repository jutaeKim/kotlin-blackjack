package blackjack.model.player

import blackjack.model.CardDistributor
import blackjack.model.card.Card
import blackjack.model.card.Cards
import blackjack.model.card.Score

class Player(val name: String, private val hitDecisionMaker: HitDecisionMaker) :
    CardRecipient {

    private val cardList = mutableListOf<Card>()

    val score: Score
        get() = Score.of(this.cardList)

    val canHit: Boolean
        get() = !this.score.isBustOrBlackJack && hitDecisionMaker.doYouWantToHit(this)

    val cards: Cards
        get() = Cards(this.cardList.toList())

    val cardCount: Int
        get() = this.cardList.size

    override fun addCard(card: Card) {
        this.cardList.add(card)
    }

    fun clearCard() {
        this.cardList.clear()
    }

    fun hitWhileWants(cardDistributor: CardDistributor, progress: ((Player) -> Unit)? = null) {
        while (this.canHit) {
            cardDistributor.giveCardsTo(this) // hit
            progress?.invoke(this)
        }
    }
}

class Players(playerList: List<Player>) : List<Player> by playerList {

    val blackJackPlayer: Player?
        get() = this.find { it.score is Score.BlackJack }

    fun startNewGame(cardDistributor: CardDistributor) {
        cardDistributor.resetCardSet()
        this.clearCard()
        cardDistributor.giveCardsTo(this, 2)
    }

    fun playGame(cardDistributor: CardDistributor, progress: ((Player) -> Unit)? = null) {
        this.forEach { player ->
            this.blackJackPlayer?.let { return@forEach }
            player.hitWhileWants(cardDistributor, progress)
        }
    }

    fun clearCard() {
        this.forEach { it.clearCard() }
    }

    companion object {
        fun List<Player>.toPlayers() = Players(this)
        fun Player.toPlayers() = Players(listOf(this))
    }
}
