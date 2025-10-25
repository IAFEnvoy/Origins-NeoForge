package com.iafenvoy.origins.event;

import net.neoforged.bus.api.Event;

public class ResultedEvent extends Event {
    private Result result;

    public ResultedEvent(Result defaultResult) {
        this.result = defaultResult;
    }

    public Result getResult() {
        return this.result;
    }

    public void allow() {
        this.result = Result.ALLOW;
    }

    public void deny() {
        this.result = Result.DENY;
    }

    public enum Result {
        ALLOW, DENY;

        public boolean allow() {
            return this == ALLOW;
        }

        public boolean deny() {
            return this == DENY;
        }
    }
}
