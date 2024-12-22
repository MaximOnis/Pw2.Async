import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

class ArrayChunkProcessor implements Callable<List<Integer>>{
    private final int[] array;
    private final int start;
    private final int end;
    private final int multiplier;

    public ArrayChunkProcessor(int[] array, int start, int end, int multiplier){
        this.array = array;
        this.start = start;
        this.end = end;
        this.multiplier = multiplier;
    }

    @Override
    public List<Integer> call(){
        CopyOnWriteArrayList<Integer> result = new CopyOnWriteArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(array[i] * multiplier);
        }
        return result;
    }
}

public class Main {

    public static void main(String[] args) {

        // Введення меж діапазону значень
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть нижню межу діапазону:");
        int lowerBound = scanner.nextInt();
        System.out.println("Введіть верхню межу діапазону:");
        int upperBound = scanner.nextInt();

        // Генерація випадкового масиву
        Random random = new Random();
        int arraySize = 40 + random.nextInt(21); // Від 40 до 60 елементів
        System.out.println("Size = " + arraySize);
        int[] array = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
        }

        // Вивід
        System.out.println("Згенерований масив: ");
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();

        // Введення множнику
        System.out.println("Введіть множник:");
        int multiplier = scanner.nextInt();

        // Початок вимірювання часу
        long startTime = System.currentTimeMillis();

        // Використання ExecutorService для обробки масиву частинами
        int chunkSize = 10; // Поділ масиву на частини по 10 елементів
        int numThreads = arraySize / chunkSize + 1;
        System.out.println(" Threads = " + numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // створення списку Future
        List<Future<List<Integer>>> futures = new ArrayList<>();

        // розбиття масиву на підмасиви та створення Future для обробки потоку
        for (int i = 0; i < array.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, array.length);
            Callable<List<Integer>> task = new ArrayChunkProcessor(array, i, end, multiplier);
            futures.add(executor.submit(task));
        }

        // Збір результатів
        List<Integer> finalResult = new CopyOnWriteArrayList<>();
        for (Future<List<Integer>> future : futures) {
            try {
                if (!future.isCancelled()) {
                    finalResult.addAll(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

        // Кінець вимірювання часу
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        // Виведення результатів
        System.out.println("Оброблений масив: ");
        for (int num : finalResult) {
            System.out.print(num + " ");
        }
        System.out.println();

        // Виведення часу виконання
        System.out.println("Час виконання: " + (endTime - startTime) + " мс");
    }
}