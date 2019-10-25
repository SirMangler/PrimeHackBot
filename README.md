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
- token= `Place your bot token here.`
- bot-controller= `Add the ID of the Role which you want to be able to use moderator commands. You can add this multiple times to add multiple roles`

# topics.cfg
This file is where topics are stored. Refer to the commands to modify this.

# Build Requirements
- JavaSE/JRE 1.8
- Maven
- Eclipse (might not be required but it was built for eclipse)
