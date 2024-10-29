package com.example.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import io.reactivex.Observable;

class Order {
    private final String product;
    private final int quantity;
    private final double price;

    public Order(String product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Producto: " + product + ", Cantidad: " + quantity + ", Precio: $" + price;
    }
}

public class OrderSystem {

    private static List<Order> orders = new ArrayList<>();
    private static List<Order> archivedOrders = new ArrayList<>();
    private static List<String> menuItems = Arrays.asList("Pizza", "Hamburguesa", "Ensalada", "Pasta", "Taco");
    private static List<Double> prices = Arrays.asList(8.5, 5.0, 4.0, 7.5, 3.0);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Pedidos predeterminados
        orders.add(new Order("Pizza", 2, 8.5));
        orders.add(new Order("Hamburguesa", 1, 5.0));
        archivedOrders.add(new Order("Ensalada", 1, 4.0));
        archivedOrders.add(new Order("Pasta", 3, 7.5));

        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menú de opciones ---");
            System.out.println("1. Crear un nuevo pedido");
            System.out.println("2. Filtrar pedidos por producto");
            System.out.println("3. Calcular total de cada pedido (map)");
            System.out.println("4. Mostrar total de ventas para un producto específico (flatMap)");
            System.out.println("5. Mostrar todos los pedidos combinados (merge)");
            System.out.println("6. Mostrar pedidos con mensajes promocionales (zip)");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (choice) {
                case 1:
                    createOrder();
                    break;
                case 2:
                    filterOrders();
                    break;
                case 3:
                    calculateOrderTotals();
                    break;
                case 4:
                    calculateTotalForProduct();
                    break;
                case 5:
                    showAllOrdersCombined();
                    break;
                case 6:
                    showOrdersWithPromotions();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
        scanner.close();
    }

    private static void createOrder() {
        boolean addingMore = true;

        while (addingMore) {
            System.out.println("\n--- Menú de Platos ---");
            for (int i = 0; i < menuItems.size(); i++) {
                System.out.println((i + 1) + ". " + menuItems.get(i) + " - $" + prices.get(i));
            }

            System.out.print("Seleccione el número del plato que desea ordenar: ");
            int productChoice = scanner.nextInt();
            if (productChoice < 1 || productChoice > menuItems.size()) {
                System.out.println("Selección no válida.");
                return;
            }

            String product = menuItems.get(productChoice - 1);
            double price = prices.get(productChoice - 1);

            System.out.print("Ingrese la cantidad: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            Order newOrder = new Order(product, quantity, price);
            orders.add(newOrder);
            System.out.println("Pedido agregado: " + newOrder);

            // Preguntar si quiere añadir otro plato
            System.out.print("¿Desea añadir otro plato a este pedido? (sí/no): ");
            String response = scanner.nextLine();
            addingMore = response.equalsIgnoreCase("sí") || response.equalsIgnoreCase("si");
        }
    }

    private static void filterOrders() {
        System.out.print("Ingrese el nombre del producto para filtrar: ");
        String product = scanner.nextLine();

        Observable.fromIterable(orders)
                .filter(order -> order.getProduct().equalsIgnoreCase(product))
                .subscribe(order -> System.out.println("Pedido encontrado: " + order),
                        throwable -> System.err.println("Error: " + throwable),
                        () -> System.out.println("Búsqueda completa."));
    }

    private static void calculateOrderTotals() {
        System.out.println("Calculando el total de cada pedido...");

        Observable.fromIterable(orders)
                .map(order -> "Pedido de " + order.getProduct() + " - Total: $" + (order.getQuantity() * order.getPrice()))
                .subscribe(System.out::println);
    }

    private static void calculateTotalForProduct() {
        System.out.print("Ingrese el nombre del producto para calcular el total de ventas: ");
        String product = scanner.nextLine();

        Observable.fromIterable(orders)
                .filter(order -> order.getProduct().equalsIgnoreCase(product))
                .flatMap(order -> Observable.just(order.getQuantity() * order.getPrice()))
                .reduce(Double::sum)
                .subscribe(total -> System.out.println("Total de ventas para " + product + ": $" + total),
                        throwable -> System.err.println("Error: " + throwable));
    }

    private static void showAllOrdersCombined() {
        System.out.println("Mostrando todos los pedidos combinados (actuales y archivados):");

        Observable<Order> currentOrders = Observable.fromIterable(orders);
        Observable<Order> archivedOrdersStream = Observable.fromIterable(archivedOrders);

        Observable.merge(currentOrders, archivedOrdersStream)
                .subscribe(order -> System.out.println(order),
                        throwable -> System.err.println("Error: " + throwable),
                        () -> System.out.println("Listado completo de pedidos combinados."));
    }

    private static void showOrdersWithPromotions() {
        System.out.println("Mostrando pedidos con mensajes promocionales (zip):");

        List<String> promotions = Arrays.asList(
                "¡Descuento especial en este pedido!",
                "¡Obtén un postre gratis en tu próxima orden!",
                "¡10% de descuento en tu próxima compra!",
                "¡Entrega gratuita si ordenas nuevamente esta semana!",
                "¡Gracias por ordenar con nosotros!"
        );

        Observable<Order> ordersObservable = Observable.fromIterable(orders);
        Observable<String> promotionsObservable = Observable.fromIterable(promotions);

        Observable.zip(ordersObservable, promotionsObservable, 
                (order, promotion) -> order + " - Promoción: " + promotion)
                .subscribe(System.out::println,
                        throwable -> System.err.println("Error: " + throwable),
                        () -> System.out.println("Promociones asignadas a los pedidos."));
    }
}
