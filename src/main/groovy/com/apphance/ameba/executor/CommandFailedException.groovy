package com.apphance.ameba.executor

class CommandFailedException extends RuntimeException {

    private Command c

    CommandFailedException(String message, Command c) {
        super(message)
        this.c = c
    }

    Command getCommand() {
        return c
    }
}
