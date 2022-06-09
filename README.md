How to include the API with Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>com.github.elijuh</groupId>
        <artifactId>ServerPingerAPI</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

How to include the API with Gradle:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compileOnly "com.github.elijuh:ServerPingerAPI:1.0"
}
```

Example Usage of ServerPinger
```java
public class Test {
    public static void main(String[] args) {
        // Creating an instance
        ServerPinger pinger = new ServerPinger();

        try {
            // Pinging hypixel.net server-list information
            ServerResponse response = pinger.ping("hypixel.net");

            // Ping in ms
            long ping = response.getPing();

            // Description of the server
            ServerResponse.Description description = response.getDescription();

            // Version of the server
            ServerResponse.Version version = response.getVersion();

            // Player information
            ServerResponse.Players players = response.getPlayers();

            int online = players.getOnline();
            int maxPlayers = players.getMax();

            // Some servers show a list of the players online, but in this example; hypixel does not.
            List<ServerResponse.Player> playerList = players.getSample();

            // You can also get the Base64 for the server icon
            String iconBase = response.getFavicon();

            // Displaying information to console
            System.out.println("Ping: " + ping + "ms");
            System.out.println("Description: " + description.getText());
            System.out.println("Version: " + version.getName());
            System.out.println("Player Count: " + online + "/" + maxPlayers);
            System.out.println("Players: " + playerList);
            System.out.println("Icon Base64: " + iconBase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```