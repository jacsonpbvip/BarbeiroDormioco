package com.mycompany.barbeirodorminhoco;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarbeariaApp {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(BarbeariaApp.class.getName());

        ReentrantLock lock = new ReentrantLock();
        Condition condicao = lock.newCondition();
        Queue<Cliente> filaDeEspera = new LinkedList<>();

        Barbeiro barbeiro = new Barbeiro(lock, condicao, filaDeEspera);
        Recepcionista recepcionista = new Recepcionista(lock, condicao, filaDeEspera, barbeiro);

        Thread barbeiroThread = new Thread(barbeiro);
        Thread recepcionistaThread = new Thread(recepcionista);

        barbeiroThread.start();
        recepcionistaThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        barbeiro.stop();
        recepcionista.stop();

        try {
            barbeiroThread.join();
            recepcionistaThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int clientesDesistiram = recepcionista.getClientesDesistiram();
        int clientesAtendidos = barbeiro.getClientesAtendidos();

        logger.log(Level.INFO, "Número de clientes que desistiram: " + clientesDesistiram);
        logger.log(Level.INFO, "Número de clientes que atendidos: " + clientesAtendidos);

        logger.log(Level.INFO, "Fim da aplicação.");
    }
}
