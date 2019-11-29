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
- warn [user] [reason] `Warns a user for the specified reason, and issues a punishment.`
- warns [user] `Lists a user's past warnings`
- gateReactRole [message-id] [@role|role-id] [emote-name|emote-id] `Adds a specified reaction to a specified message, when this reaction is clicked it will add the specified role. This is designed to grant entry to a server.`

# primebot.cfg
- token= `Place your bot token here.`
- bot-controller= `Add the ID of the Role which you want to be able to use moderator commands. You can add this multiple times to add multiple roles`
- gate-emote `The id of the emote used in the gateway`
- gate-role `This role issued by the gateway button.`
- gate-react-message `The message id that the reaction is associated with`

# topics.cfg
This file is where topics are stored. Refer to the commands to modify this.

# Build Requirements
- JavaSE/JRE 1.8
- Maven
- Eclipse (might not be required but it was built for eclipse)
