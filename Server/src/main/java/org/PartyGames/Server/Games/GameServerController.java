package org.PartyGames.Server.Games;

import com.google.errorprone.annotations.ForOverride;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Connections.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Map;


public abstract class GameServerController {
    private static final Logger log = LoggerFactory.getLogger(GameServerController.class);
    protected WebSocketServer connection;
    protected int TPS;
    protected boolean is_finished;
    protected Map<String, String> client_names;

    public GameServerController() {
        this.connection = null;
        this.TPS = 0;
        is_finished = false;
        this.client_names = null;
    }

    public final GameServerController attachWebSocketServer(WebSocketServer connection) {
        this.connection = connection;
        return this;
    }
    public final GameServerController attachTPS(int TPS) {
        log.info("Set TPS to: {}, for: {}", TPS, GameServerController.class);
        this.TPS = TPS;
        return this;
    }
    public final GameServerController attachClientNames(@Nullable Map<String, String> client_names) {
        this.client_names = client_names;
        return this;
    }

    public final boolean isFinished() { return is_finished; }

    @OverridingMethodsMustInvokeSuper
    public void start() { }
    @OverridingMethodsMustInvokeSuper
    public void stop() { is_finished = true; }

    @ForOverride
    public abstract void handleGame(List<NetworkMessage> messages, int tick);

}
