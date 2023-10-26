package com.mycompany.barbeirodorminhoco;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;

class SalaEspera {
    private ReentrantLock lock;
    private Condition condicao;
    private Queue<Cliente> filaDeEspera;
    private int capacidadeMaxima;

    public SalaEspera(ReentrantLock lock, Condition condicao, int capacidadeMaxima) {
        this.lock = lock;
        this.condicao = condicao;
        this.filaDeEspera = new LinkedList<>();
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public void adicionarCliente(Cliente cliente) {
        Logger logger = Logger.getLogger(SalaEspera.class.getName());

        lock.lock();
        try {
            if (filaDeEspera.size() < capacidadeMaxima) {
                filaDeEspera.add(cliente);
                logger.log(Level.INFO, "Cliente #" + cliente.getId() + " entrou na sala de espera.");
                condicao.signal(); // Se o barbeiro estiver dormindo, acorde-o
            } else {
                logger.log(Level.INFO,
                        "Sala de espera lotada. Cliente #" + cliente.getId() + " desistiu e foi embora.");
            }
        } finally {
            lock.unlock();
        }
    }

    public Cliente proximoCliente() {
        Logger logger = Logger.getLogger(SalaEspera.class.getName());

        lock.lock();
        try {
            while (filaDeEspera.isEmpty()) {
                logger.log(Level.INFO, "Sala de espera vazia. Aguardando cliente.");
                condicao.await(); // Aguarda até que um cliente entre na sala de espera
            }
            Cliente cliente = filaDeEspera.poll();
            logger.log(Level.INFO, "Cliente #" + cliente.getId() + " está na cadeira do barbeiro.");
            return cliente;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
