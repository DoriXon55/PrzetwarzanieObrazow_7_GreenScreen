import org.opencv.core.Core;

import java.util.Scanner;

public class Main{
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }
    public static void main(String[] args) {
        OpenCvFunctions openCvFunctions = new OpenCvFunctions();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz filtr:\n" +
                "1. Użyj filtru Gaussa\n" +
                "2. Użyj filtru Medianowego\n" +
                "3. Użyj morfologicznego otwarcia i domknięcia\n");
        int userOption = scanner.nextInt();
        openCvFunctions.algorytm(userOption);

    }
}
