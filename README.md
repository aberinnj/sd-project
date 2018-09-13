# sdprojectrisk
Software Design class team project implementing a clone of the boardgame Risk.

__Files and Program Flow__

- GameManager - Handles all calls to setup, start, stop and restart a game.
- BoardManager - Handles all board-related requests and responses. Also keeps track of Board pieces, cards, events
- Player - Handles all events and requests involving player information
- Army - Counter for game pieces
- Dice - Simple Dice class
- Territory - Handles all requests and responses involving Territories such as occupants, occupiedBy, isOccupied as well as neighboring territories
- Deck


__Summary__

* GameManager sets up board, players, decks, and starts the game loop
* BoardManager sets up Territories, and makes use of dices
