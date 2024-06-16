import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import org.sonatype.nexus.common.event.EventManager

import java.util.concurrent.atomic.AtomicBoolean

// a simple long task
class LongTask {

    AtomicBoolean stopFlag = new AtomicBoolean(false)
    int size = 1000
    def log = LoggerFactory.getLogger(this.getClass())
    EventManager eventManager

    LongTask(container) {
        eventManager = container.lookup(org.sonatype.nexus.common.event.EventManager)
        // register listener
        register()
    }

    private void register() {
        log.info("register event")
        eventManager.register(this)
    }
    private void unregister() {
        log.info("unregister event")
        eventManager.unregister(this)
    }

    void run() {
        try {
            def i = 0
            while (i++ < size) {
                if (isCanceled()) {
                    log.info("task is canceled, force stop")
                    break
                }
                log.info("long task: " + i)
                Thread.sleep(1000)
            }
        } finally {
            unregister()
        }
    }

    boolean isCanceled() {
        return stopFlag.get()
    }

    @Subscribe
    @AllowConcurrentEvents
    void on(final String event) {
        log.info("receive event: {}", event);
        if ("stop-long-task".equals(event)) {
            stopFlag.set(true)
        }
    }
}


def task = new LongTask(container)
// run task
task.run()

