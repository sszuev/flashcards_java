#### Speaker

A module that responsible for the voicing words, i.e. it is text-to-speech service.

Right now there are two independent ways to receive audio-resources.
- the primary way is local audio-files, that are stored in the directory [AUDIO](../audio) in the form of tar archives, which could be downloaded from http://download.shtooka.net/.
- for those words for which no audio is found, the system calls an external api - http://www.voicerss.org/   
**Important**: to make it work please obtain voice rss api key and specify it using vm-option `app.speaker.voicerss.key` 

