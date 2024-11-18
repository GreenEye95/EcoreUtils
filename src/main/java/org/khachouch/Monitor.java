package org.khachouch;

import org.apache.logging.log4j.core.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;

public class Monitor extends BasicMonitor {
    private Logger logger;

    public Monitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void beginTask(String name, int totalWork) {
        if (name != null && !name.isEmpty()) {
            log(">>> " + name);
        }
    }

    @Override
    public void setTaskName(String name) {
        if (name != null && !name.isEmpty()) {
            log("<>> " + name);
        }
    }

    @Override
    public void subTask(String name) {
        if (name != null && !name.isEmpty()) {
            log(">>  " + name);
        }
    }

    @Override
    public void setBlocked(Diagnostic reason) {
        super.setBlocked(reason);
        log("#>  " + reason.getMessage());
    }

    @Override
    public void clearBlocked() {
        log("=>  " + getBlockedReason().getMessage());
        super.clearBlocked();
    }

    private void log(String msg) {
        this.logger.info(msg);
    }
}
