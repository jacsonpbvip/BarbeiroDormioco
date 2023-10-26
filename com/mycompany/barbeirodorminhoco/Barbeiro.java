package com.mycompany.barbeirodorminhoco;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;

class Barbeiro implements Runnable {
    private ReentrantLock lock;
    private Condition condicao;
    private Queue<Cliente> filaDeEspera;
    private boolean done = false;
    private int clientesAtendidos = 0;
    private Logger logger = Logger.getLogger(Barbeiro.class.getName());

    public Barbeiro(ReentrantLock lock, Condition condicao, Queue<Cliente> filaDeEspera) {
        this.lock = lock;
        this.condicao = condicao;
        this.filaDeEspera = filaDeEspera;
    }

    public void run() {
        while (!done) {
            lock.lock();
            try {
                while (filaDeEspera.isEmpty()) {
                    logger.log(Level.INFO, "O barbeiro está dormindo.");
                    condicao.await();
                }

                Cliente cliente = filaDeEspera.poll();
                logger.log(Level.INFO, "Iniciando corte de cabelo para o Cliente #" + cliente.getId());

                int tempoDeCorte = new Random().nextInt(10);
                Thread.sleep(tempoDeCorte);

                logger.log(Level.INFO, "Corte de cabelo concluído para o Cliente #" + cliente.getId());

                clientesAtendidos++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    public void stop() {
        done = true;
    }

    public int getClientesAtendidos() {
        return clientesAtendidos;
    }
}
