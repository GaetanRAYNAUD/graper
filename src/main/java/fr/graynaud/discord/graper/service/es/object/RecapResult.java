package fr.graynaud.discord.graper.service.es.object;

import fr.graynaud.discord.graper.model.EsMessage;

import java.util.Map;

public class RecapResult {

    private EsMessage randomMessage;

    private Map<String, Long> authors;

    private Map<String, Long> channels;

    private Map<String, Long> words;

    private Long nbMessage;

    public EsMessage getRandomMessage() {
        return randomMessage;
    }

    public void setRandomMessage(EsMessage randomMessage) {
        this.randomMessage = randomMessage;
    }

    public Map<String, Long> getAuthors() {
        return authors;
    }

    public void setAuthors(Map<String, Long> authors) {
        this.authors = authors;
    }

    public Map<String, Long> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, Long> channels) {
        this.channels = channels;
    }

    public Map<String, Long> getWords() {
        return words;
    }

    public void setWords(Map<String, Long> words) {
        this.words = words;
    }

    public Long getNbMessage() {
        return nbMessage;
    }

    public void setNbMessage(Long nbMessage) {
        this.nbMessage = nbMessage;
    }
}
