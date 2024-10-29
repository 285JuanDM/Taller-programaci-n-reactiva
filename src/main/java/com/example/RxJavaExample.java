package com.example;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Hello world!
 *
 */
public class RxJavaExample
{
    public static void main( String[] args )
    {
        // Creamos un Observable que emita una lis de número de 1 al 10
        Observable<Integer> observable = Observable.range(1, 10);

        // Aplicar operadores
        observable.filter(item -> item % 2 == 0)  // Filtrar los números pares
            .map(item -> "Number: " + item) // Mapear los números a un String
            .flatMap(item -> Observable.just(item).subscribeOn(Schedulers.computation()))  // Emitir en un hilo de computación
            .doOnNext(item -> System.out.println("Processing item on thread: " + item)) // Loggear el hilo de ejecución
            .observeOn(Schedulers.newThread()) // Cambiar al hilo de ejecución
            .subscribe(new Observer<String>(){
                @Override
                public void onSubscribe(Disposable d) {
                    System.out.println("Subscribed");
                }

                @Override
                public void onNext(String item) {
                    System.out.println("Received: " + item);
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("Error: " + e.getMessage());
                }

                @Override
                public void onComplete() {
                    System.out.println("Completed");
                }
            
            }); // Suscribirse al Observable

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
