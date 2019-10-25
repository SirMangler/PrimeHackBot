# Adam Malkocich Bot
The Adam Malkocich bot made for the [PrimeHack Discord](https://discord.gg/hYp5Naz).

[Here is an example/guide on how to make an auto-reponse topic.](https://github.com/SirMangler/PrimeHackBot/wiki/Creating-a-decent-topic.)

# Commands
- ping   `Pong!`
- addTopic [topic] `Adds the topic.`
- removeTopic [topic] `Removes the topic`
- getTopic [topic] `Displays the moderator view for the topic`
- setAnswer [topic] [answer] `Assigns an answer/description to the topic`
- setWikiLink [topic] `Sets the wiki link (optional)`
- addPattern [topic] [pattern] `Adds a Regex Pattern to the detector`
- removePattern [topic] [index] `Removes a Regex Pattern from the detector`
- listTopics `Lists all topics`
- commands `Shows this list`

# primebot.cfg
- token= `Place your token here.`
- bot-controller= `Add the role id for who you want to control this bot. You can add this multiple times to add multiple roles`

# topics.cfg
This file is where topics are stored. Refer to the commands to modify this.
