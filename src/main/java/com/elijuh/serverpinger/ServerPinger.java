package com.elijuh.serverpinger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Getter
public class ServerPinger {
    private final Gson gson = new Gson();

    public ServerResponse ping(String hostname) throws IOException {
        return ping(ServerPingerOptions.builder().hostname(hostname).build());
    }

    public ServerResponse ping(ServerPingerOptions options) throws IOException {
        if (options.getHostname() == null) {
            throw new IllegalArgumentException("hostname cannot be null");
        }

        String hostname = options.getHostname();
        int port = options.getPort();

        try {
            Record[] records = new Lookup(String.format("_minecraft._tcp.%s", hostname), Type.SRV).run();

            if (records != null) {
                for (Record record : records) {
                    SRVRecord srv = (SRVRecord) record;

                    hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
                    port = srv.getPort();
                }

            }
        } catch (TextParseException e) {
            e.printStackTrace();
        }

        String json;
        long ping;

        try (Socket socket = new Socket()) {
            long start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(hostname, port), options.getTimeout());
            ping = System.currentTimeMillis() - start;

            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                 DataOutputStream handshake = new DataOutputStream(bytes)) {

                handshake.writeByte(0x00);
                Utils.writeVarInt(handshake, -1);
                Utils.writeVarInt(handshake, options.getHostname().length());
                handshake.writeBytes(options.getHostname());
                handshake.writeShort(options.getPort());
                Utils.writeVarInt(handshake, 1);

                Utils.writeVarInt(out, bytes.size());
                out.write(bytes.toByteArray());

                out.writeByte(0x01);
                out.writeByte(0x00);

                Utils.readVarInt(in);
                Utils.readVarInt(in);

                int length = Utils.readVarInt(in);

                byte[] data = new byte[length];
                in.readFully(data);
                json = new String(data, options.getCharset());

                out.writeByte(0x09);
                out.writeByte(0x01);
                out.writeLong(System.currentTimeMillis());

                Utils.readVarInt(in);
                Utils.readVarInt(in);
            }
        }

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonElement descriptionJsonElement = jsonObject.get("description");

        if (!descriptionJsonElement.isJsonObject()) {
            String description = descriptionJsonElement.getAsString();
            JsonObject descriptionJsonObject = new JsonObject();
            descriptionJsonObject.addProperty("text", description);
            jsonObject.add("description", descriptionJsonObject);

        }

        ServerResponse response = gson.fromJson(jsonObject, ServerResponse.class);
        response.setPing(ping);

        return response;
    }
}
