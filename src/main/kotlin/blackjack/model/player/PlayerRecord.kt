package blackjack.model.player

import blackjack.model.card.State

data class PlayerRecord(val player: Player, val win: Int = 0, val lose: Int = 0, val draw: Int = 0)
data class PlayerRecords(val playerRecordList: List<PlayerRecord>) : List<PlayerRecord> by playerRecordList {

    companion object {
        fun of(dealer: Player.Dealer, guests: Players): PlayerRecords {

            val guestRecords = guests.map { createGuestRecord(dealer.state, it) }
            val dealerRecord = createDealerRecord(dealer, guestRecords)

            return PlayerRecords(listOf(dealerRecord) + (guestRecords))
        }

        private fun createDealerRecord(dealer: Player.Dealer, guestRecords: List<PlayerRecord>): PlayerRecord {
            val drawCount = guestRecords.count { it.draw == 1 }
            val dealerLostCount = guestRecords.count { it.win == 1 }
            val dealerWinCount = guestRecords.count() - dealerLostCount - drawCount
            return PlayerRecord(dealer, win = dealerWinCount, lose = dealerLostCount, draw = drawCount)
        }

        private fun createGuestRecord(dealerState: State, guest: Player): PlayerRecord {
            val guestState = guest.state
            return when {
                dealerState is State.Bust -> PlayerRecord(guest, win = 1)
                guestState is State.Bust -> PlayerRecord(guest, lose = 1)
                guestState.finalScore > dealerState.finalScore -> PlayerRecord(guest, win = 1)
                guestState.finalScore < dealerState.finalScore -> PlayerRecord(guest, lose = 1)
                else -> PlayerRecord(guest, draw = 1)
            }
        }
    }
}
