# Project sdprojectrisk [![Build Status](https://travis-ci.com/aberinnj/sd_project.svg?branch=master)](https://travis-ci.com/aberinnj/sd_project) [![codecov](https://codecov.io/gh/aberinnj/sd_project/branch/master/graph/badge.svg)](https://codecov.io/gh/aberinnj/sd_project)

Software Design class team project implementing a clone of the boardgame Risk.

# Running the game
Only one instance must be run at all times for v5's chatbot. Multiple games can then be run.
To properly set this up, please provide your token and botname inside a props file. Do __not__ share your props file.

## Design
* [Chatbot]()
* [Twitter]()


# Update Log
__V.0.5__
- [ ] Timeout: A player has only 30 seconds to take an action, otherwise the game moves on to the next player
- [ ] Telegram Chatbot: Players can play using a Chatbot. At first, player identifies the game session that it wants to play by entering a gameId. A game starts when all players have joined the game. Assume that the number of players in this case is always 3. (Resources: https://core.telegram.org/bots/samples, https://monsterdeveloper.gitbooks.io/writing-telegram-bots-on-java/chapter1.html)

__V.0.4__
- [X] Notify players if their territories are under attack. (Remember the Observer Pattern)
- [X] Players can purchase in-game credit. They can use the credit to buy cards, buy undo actions, or transfer the credits to another player.
- [X] Post the number of territories conquered by each player on Twitter after each turn and at the end of the game. (https://developer.twitter.com/en/docs/developer-utilities/twitter-libraries.html or http://twitter4j.org/en/index.html )
- [X] Modify pom.xml to generate JavaDocs and class diagrams. Reference: https://maven.apache.org/plugins/maven-javadoc-plugin/examples/alternate-doclet.html

__V.0.3__
- [X] Users can undo their actions
- [ ] Your Program uses Amazon S3 to replay games (You get some Amazon credits through your GitHub student pack)
- [X] Test cases to cover at least 50% of the code. 
- [X] Show test coverage on the repository page:
https://blog.frankel.ch/travis-ci-tutorial-for-java-projects/
