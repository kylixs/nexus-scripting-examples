import org.sonatype.nexus.common.event.EventManager

// send message
log.info("sending event: 'stop-long-task' ")
EventManager eventManager = container.lookup(org.sonatype.nexus.common.event.EventManager)
eventManager.post("stop-long-task")
