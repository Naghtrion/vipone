package me.subzero0.vipone.async;

import java.util.concurrent.ConcurrentLinkedQueue;

class AsyncQueue extends Thread {

    private final ConcurrentLinkedQueue<Runnable> queue;
    private boolean stop = false;

    protected AsyncQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty() && !stop) {
                    try {
                        queue.wait();
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
            if (stop) {
                return;
            }
            try {
                Runnable r = queue.poll();
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addQueue(Runnable r) {
        synchronized (queue) {
            queue.offer(r);
            queue.notify();
        }
    }

    public void kill() {
        stop = true;
        synchronized (queue) {
            queue.notify();
        }
    }

}
