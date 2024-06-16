package com.thospfuller

import java.sql.Statement
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.DriverManager

import java.util.concurrent.LinkedBlockingQueue

import java.util.concurrent.TimeUnit

import org.h2.tools.TriggerAdapter

@Singleton
public class QueueSingleton {

    private static final def queueSingleton = new LinkedBlockingQueue<String>()

    public static LinkedBlockingQueue<String> getInstance() {
        return queueSingleton
    }
}

def pollingThread = new Thread({

    def ctr = 0

    def queueSingleton = QueueSingleton.getInstance()

    def result = null

    while ("end;" != result) {

        result = queueSingleton.take()

        println "pollingThread.iteration[${ctr++}] -> result: $result"
    }
})

pollingThread.name = "Polling Thread"

pollingThread.start()

public class Notifier extends TriggerAdapter {

    final def queueSingleton = QueueSingleton.getInstance()

    def ctr = 0

    public void fire(Connection connection, ResultSet oldRow, ResultSet newRow) throws SQLException {

        println "notifier.queue.size: ${queueSingleton.size()}; ctr[${ctr++}]: oldRow: $oldRow, newRow: $newRow"

        String id = newRow?.getString("id")

        if (id != null)
            queueSingleton.offer("updated id: $id")
    }
}

public class Main {

    def connection = DriverManager.getConnection("jdbc:h2:mem:test")

    void stop() {
        connection.close()
    }

    public void run() {

        def statement = connection.createStatement()

        try {

            statement.execute("create table t_test(id identity);")

            statement.execute('create trigger if not exists my_notifier_trigger after insert on t_test for each row call "com.thospfuller.Notifier" ')

            for (int ctr in 0..25) {
                println("insert data: " + ctr)
                statement.execute("insert into t_test (id) values ($ctr); commit;")
                Thread.sleep(500)
            }
        } finally {
            statement.close()
        }
    }
}

def main = new Main()

main.run()

def queueSingleton = QueueSingleton.getInstance()

queueSingleton.offer("end;")

pollingThread?.join()

main.stop()

println "...done!"

return