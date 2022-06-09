package com.elijuh.serverpinger;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ServerPingerOptions {

    @Getter
    private String hostname;

    @Getter
    @Builder.Default
    private String charset = "UTF-8";

    @Getter
    @Builder.Default
    private int port = 25565;

    @Getter
    @Builder.Default
    private int timeout = 5000;
}
