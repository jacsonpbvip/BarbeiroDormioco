package com.mycompany.barbeirodorminhoco;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class Recepcionista implements Runnable {
    private ReentrantLock lock;
    private Condition condicao;
    private Queue<Cliente> filaDeEspera;
    private boolean done = false;
    private int clientesDesistiram = 0;

    public Recepcionista(ReentrantLock lock, Condition condicao, Queue<Cliente> filaDeEspera, Barbeiro barbeiro) {
        this.lock = lock;
        this.condicao = condicao;
        this.filaDeEspera = filaDeEspera;
    }

    public void run() {
        while (!done) {
            lock.lock();
            try {
                Cliente novoCliente = new Cliente(lock, condicao, filaDeEspera); 
                int tempoDeAtendimento = new Random().nextInt(10);
                Thread.sleep(tempoDeAtendimento);

                if (filaDeEspera.size() < 5) { 
                    System.out.println("Cliente #" + novoCliente.getId() + " chegou à barbearia.");
                    filaDeEspera.add(novoCliente);
                    condicao.signal(); 
                } else {
                    System.out.println("Sala de espera lotada. Cliente #" + novoCliente.getId() + " desistiu e foi embora.");
                    clientesDesistiram++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void stop() {
        done = true;
    }

    public int getClientesDesistiram() {
        return clientesDesistiram;
    }
}
