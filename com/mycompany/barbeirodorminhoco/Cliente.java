package com.mycompany.barbeirodorminhoco;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;

class Cliente implements Runnable {
    private static int proximoId = 1;
    private int id;
    private ReentrantLock lock;
    private Condition condicao;
    private Queue<Cliente> filaDeEspera;

    public Cliente(ReentrantLock lock, Condition condicao, Queue<Cliente> filaDeEspera) {
        this.lock = lock;
        this.condicao = condicao;
        this.filaDeEspera = filaDeEspera;
        this.id = proximoId++;
    }

    public int getId() {
        return id;
    }

    public void run() {
        Logger logger = Logger.getLogger(Cliente.class.getName());

        lock.lock();
        int cadeiras = 5;
        try {
            if (filaDeEspera.size() <= cadeiras) { 
                logger.log(Level.INFO, "Cliente #" + id + " chegou à barbearia.");
                filaDeEspera.add(this);
                condicao.signal();
            } else {
                logger.log(Level.INFO, "Não há cadeiras disponíveis. Cliente #" + id + " desistiu e foi embora.");
                return;
            }
            
            condicao.await(); 

            logger.log(Level.INFO, "Cliente #" + id + " está pronto para o corte de cabelo.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
