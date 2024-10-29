package com.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ReactorExample {

    public static void main(String[] args) {
        // Creamos un Flux que emita una lista de números de 1 al 10
        Flux<Integer> flux = Flux.range(1, 10);

        // Aplicar operadores
        flux.filter(item -> item % 2 == 1) // Filtrar los números impares
            .map(item -> "Number: " + item) // Mapear los números a un String
            .flatMap(item -> Mono.just(item).subscribeOn(Schedulers.parallel())) // Emitir en un hilo de computación
            .doOnNext(item -> System.out.println("Processing item on thread: " + item)) // Loggear el hilo de ejecución
            .subscribeOn(Schedulers.boundedElastic()) // Ejecutar en un hilo elástico
            .subscribe(
                item -> System.out.println("Received: " + item), // Imprimir el item recibido
                error -> System.out.println("Error: " + error.getMessage()), // Imprimir el error
                () -> System.out.println("Completed") // Imprimir que se completó
            ); // Suscribirse al Flux
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
