package com.elijuh.serverpinger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class ServerResponse {
    private final Description description;
    private final Players players;
    private final Version version;

    @Setter
    private String favicon;

    @Setter
    private long ping;

    @Getter
    @ToString
    public static class Description {
        private String text;
    }

    @Getter
    @ToString
    public static class Players {
        private int max;
        private int online;
        private List<Player> sample;
    }

    @Getter
    @ToString
    public static class Player {
        private String name;
        private String id;
    }

    @Getter
    @ToString
    public static class Version {
        private String name;
        private int protocol;
    }
}
