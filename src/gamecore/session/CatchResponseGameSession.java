package gamecore.session;

import gamecore.message.GameResponse;

import java.util.Set;

/**
 * 用于拦截响应消息。
 */
public class CatchResponseGameSession implements GameSession {

    private GameResponse resp;

    public GameResponse getResponse() {
        return resp;
    }
    
    @Override
    public void write(GameResponse message) {
        resp = message;
    }

    @Override
    public void broadcast(GameResponse message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object setAttribute(Object o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Object> getAttributeKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
