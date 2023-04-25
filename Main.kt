package indigo
val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = listOf("♦", "♥", "♠", "♣")
var deck:MutableList<String> = mutableListOf()
var deckTable:MutableList<String> = mutableListOf()
var deckP1:MutableList<String> = mutableListOf()
var deckP2:MutableList<String> = mutableListOf()
var P2Cards = 0
var P1Cards = 0
var P2Score = 0
var P1Score = 0
var lastCardsGoP1 = false//0 nobody, 1 = P1, 2 = P2
var notExit = true
const val numCardToPlayers = 6


fun buildDeck(){
    P2Cards = 0
    P1Cards = 0
    P2Score = 0
    P1Score = 0
    deck.clear()
    for(suit in suits){
        for(rank in ranks){
            if(ranks.last() == rank && suits.last() == suit){
                deck.add("$rank$suit")
            }else {
                deck.add("$rank$suit")
            }
        }
    }
    deck.shuffle()
}

fun resetOption(){
    buildDeck()
    println("Card deck is reset.")
}
fun shuffleOption(){
    deck.shuffle()
    println("Card deck is shuffled.")
}
fun getOption(number: Int){
    if(number > deck.size){
        println("The remaining cards are insufficient to meet the request.")
    }else{
        deckTable.addAll(deck.subList(0, number))
        println(deck.subList(0, number).joinToString(separator = " "))
        deck = deck.subList(number, deck.size)
    }

}

fun giveCardsToPlayers(){
    deckP1.addAll(deck.subList(0, numCardToPlayers))
    deck = deck.subList(numCardToPlayers, deck.size)
    deckP2.addAll(deck.subList(0, numCardToPlayers))
    deck = deck.subList(numCardToPlayers, deck.size)
}
fun addCardScore(isPlayer:Boolean, lastTime:Boolean = false){
    lastCardsGoP1 = isPlayer
    if(isPlayer){
        P1Cards += deckTable.size
        P1Score += deckTable.count { it.substring(0, it.length-1) in listOf("A","10","J","Q", "K")  }
    }else{
        P2Cards += deckTable.size
        P2Score += deckTable.count { it.substring(0, it.length-1) in listOf("A","10","J","Q", "K")  }
    }
    deckTable.clear()
    if(lastTime){
        if(P1Cards > P2Cards){
            P1Score += 3
        }else{
            P2Score += 3
        }
    }
    println("Score: Player $P1Score - Computer $P2Score\n" +
            "Cards: Player $P1Cards - Computer $P2Cards")
}
fun getCandidateCards():List<String>{
    var candidates = mutableListOf<String>()
    for(card in deckP2) {
        if (deckTable.isNotEmpty() && (card.last() == deckTable.last().last() || card.substring(0, card.length - 1) == deckTable.last().substring(0, deckTable.last().length - 1))
        ) {
            candidates.add(card)
        }
    }
    return candidates
}
fun computerStrategy():String{
    //1)
    if(deckP2.size == 1){
        return deckP2[0]
    }
    val candidates = getCandidateCards()
    //2)
    if(candidates.size == 1){
        return candidates[0]
    }
    //3) || 4)
    if(deckTable.isEmpty() || candidates.isEmpty()){
        val suits = deckP2.groupBy { it.last() }
        for (suit in suits){
            if (suit.value.size > 1){
                return suit.value.random()//at random or first?
            }
        }
        val ranks = deckP2.groupBy { it.substring(0,it.length-1) }
        for (rank in ranks){
            if(rank.value.size > 1){
                return rank.value.random()
            }
        }
        return deckP2.random()
    }
    //5)
    val suits = candidates.groupBy { it.last() }
    for (suit in suits){
        if (suit.value.size > 1){
            return suit.value.random()//at random or first?
        }
    }
    val ranks = candidates.groupBy { it.substring(0,it.length-1) }
    for(rank in ranks){
        if(rank.value.size > 1){
            return rank.value.random()
        }
    }
    return candidates.random()

}
fun main() {
    var input = ""
    buildDeck()
    //start game
    println("Indigo Card Game")
    println("Play first?")
    var playFirst = readln().lowercase()
    while(playFirst != "yes" && playFirst != "no"){
        println("Play first?")
        playFirst = readln().lowercase()
    }
    var isPlayer = playFirst == "yes"
    lastCardsGoP1 = isPlayer

    print("Initial cards on the table: ")
    getOption(4)
    giveCardsToPlayers()
    println()
    while(true) {
        if(deckTable.size > 0) {
            println("${deckTable.size} cards on the table, and the top card is ${deckTable.last()}")
        }else{
            println("No cards on the table")
        }
        if (isPlayer && deckP1.size > 0) {
            println(
                "Cards in hand: ${deckP1.mapIndexed { i, s -> "${i + 1})$s"}.joinToString(" ")}\n" +
                        "Choose a card to play (1-${deckP1.size}):"
            )
            input = readln()
            while(input.toIntOrNull() !in 1..deckP1.size){
                if(input == "exit"){
                    break
                }
                println("Choose a card to play (1-${deckP1.size}):")
                input = readln()
            }
            if(input == "exit"){
                break
            }
            val choice = input.toInt()
            //win cards
            if(deckTable.isNotEmpty() && (deckP1[choice-1].last() == deckTable.last().last() || deckP1[choice-1].substring(0,deckP1[choice-1].length-1) == deckTable.last().substring(0,deckTable.last().length-1))){
                println("Player wins cards")
                deckTable.add(deckP1[choice - 1])
                addCardScore(isPlayer = true)
            }else {
                deckTable.add(deckP1[choice - 1])
            }
            deckP1.removeAt(choice - 1)
            println()
        } else if(deckP2.size > 0){//computer
            println(deckP2.joinToString(" "))
            //COMPUTER STRATEGY:
            val computerCard = computerStrategy()
            println("Computer plays $computerCard")
            //win cards
            if(deckTable.isNotEmpty() && (computerCard.last() == deckTable.last().last() || computerCard.substring(0,computerCard.length-1) == deckTable.last().substring(0,deckTable.last().length-1))){
                println("Computer wins cards")
                deckTable.add(computerCard)
                addCardScore(isPlayer = false)
            }else {
                deckTable.add(computerCard)
            }
            deckP2.remove(computerCard)
            println()
        }
        //if no card in hand
        if (deckP1.isEmpty() && deckP2.isEmpty() && deck.size == 0) {
            if(deckTable.size > 0) {
                println("${deckTable.size} cards on the table, and the top card is ${deckTable.last()}")
            }else{
                println("No cards on the table")
            }
            break//end game
        }
        if (isPlayer && deckP1.isEmpty() && deck.size >= numCardToPlayers) {
            deckP1.addAll(deck.subList(0, numCardToPlayers))
            deck = deck.subList(numCardToPlayers, deck.size)
        }
        if (!isPlayer && deckP2.isEmpty() && deck.size >= numCardToPlayers) {
            deckP2.addAll(deck.subList(0, numCardToPlayers))
            deck = deck.subList(numCardToPlayers, deck.size)
        }
        isPlayer = !isPlayer
    }
    if(input != "exit") {
        if (deckTable.size > 0) {
            addCardScore(lastCardsGoP1, lastTime = true)
        } else {
            if (P1Cards > P2Cards) {
                P1Score += 3
            } else {
                P2Score += 3
            }
            println(
                "Score: Player $P1Score - Computer $P2Score\n" +
                        "Cards: Player $P1Cards - Computer $P2Cards"
            )
        }
    }
    println("Game Over")
}