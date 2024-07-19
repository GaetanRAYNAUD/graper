package fr.graynaud.discord.graper.model;

import java.util.List;

public class EsMessageReaction {

    private String name;

    private List<Long> usersIds;

    public EsMessageReaction() {
    }

    public EsMessageReaction(String name, List<Long> usersIds) {
        this.name = name;
        this.usersIds = usersIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Long> usersIds) {
        this.usersIds = usersIds;
    }
}
