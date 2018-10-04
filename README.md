# sdprojectrisk [![Build Status](https://travis-ci.com/aberinnj/sd_project.svg?branch=master)](https://travis-ci.com/aberinnj/sd_project)
[![codecov](https://codecov.io/gh/aberinnj/sd_project/branch/master/graph/badge.svg)](https://codecov.io/gh/aberinnj/sd_project)
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


# Update Log
__V.3.0__
- Users can undo their actions
- Your Program uses Amazon S3 to replay games (You get some Amazon credits through your GitHub student pack)
- Test cases to cover at least 50% of the code. 
- Show test coverage on the repository page:
https://blog.frankel.ch/travis-ci-tutorial-for-java-projects/
