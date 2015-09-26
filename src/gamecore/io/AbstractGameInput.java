package gamecore.io;

import java.util.Date;

public abstract class AbstractGameInput implements GameInput {

    @Override
    public Date getDate() {
        long time = getInt() * 1000L;
        return new Date(time);
    }
    
}
