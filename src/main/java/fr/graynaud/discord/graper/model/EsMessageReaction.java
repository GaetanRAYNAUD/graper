package fr.graynaud.discord.graper.model;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;
import java.util.Objects;

public class EsMessageReaction {

    private String name;

    @Field(name = "users_ids")
    private List<Long> usersIds;

    @Field(name = "nb_users")
    private int nbUsers;

    public EsMessageReaction() {
    }

    public EsMessageReaction(String name, List<Long> usersIds) {
        this.name = name;
        this.usersIds = usersIds;
        this.nbUsers = CollectionUtils.size(usersIds);
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

    public int getNbUsers() {
        return nbUsers;
    }

    public void setNbUsers(int nbUsers) {
        this.nbUsers = nbUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EsMessageReaction that = (EsMessageReaction) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
