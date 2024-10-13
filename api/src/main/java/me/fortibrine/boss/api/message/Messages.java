package me.fortibrine.boss.api.message;

import lombok.Getter;

@Getter
public enum Messages implements Message {

    BOSS_KILLED("boss-killed");

    private final String path;

    private Messages(String path) {
        this.path = path;
    }

}
