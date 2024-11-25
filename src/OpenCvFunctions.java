import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class OpenCvFunctions {
    private final static String outputPath = "C:\\Users\\doria\\IdeaProjectsp\\PrzetwarzanieObrazow_7_GreenScreen\\src\\";

    public void displayOpenImage(String filePath) {
        ImageIcon openImage = new ImageIcon(filePath);
        JLabel openLabel = new JLabel(openImage);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(openLabel);
        frame.pack();
        frame.setVisible(true);
    }
    public void saveImage(Mat image, String outputFileName) {
        String finalPath = outputPath + outputFileName;
        boolean result = Imgcodecs.imwrite(finalPath, image);
        if (result) {
            System.out.println("Poprawnie zapisano");
            displayOpenImage(finalPath);
        } else {
            System.out.println("Nie zapisano pliku");
        }
    }


    public static void applyImageOnChessboard(String chessboardImagePath, String userImagePath) {
        // Wczytanie obrazów
        Mat chessboardImage = Imgcodecs.imread(chessboardImagePath);
        Mat userImage = Imgcodecs.imread(userImagePath);

        if (chessboardImage.empty() || userImage.empty()) {
            System.out.println("Error: Could not load images. Check file paths.");
            return;
        }

        // Zakładamy, że obraz szachownicy jest podzielony na równe kwadratowe pola
        // Zmienna, która określa liczbę pól w szachownicy (np. 8x8 dla klasycznej szachownicy)
        int rows = 8;
        int cols = 8;

        // Obliczanie wymiarów jednego pola na szachownicy
        int fieldWidth = chessboardImage.cols() / cols;
        int fieldHeight = chessboardImage.rows() / rows;

        // Zmiana rozmiaru obrazu użytkownika, aby pasował do wymiarów pola szachownicy
        Mat resizedUserImage = new Mat();
        Size targetSize = new Size(fieldWidth, fieldHeight);
        Imgproc.resize(userImage, resizedUserImage, targetSize, 0, 0, Imgproc.INTER_AREA);

        // Wybieramy miejsce, w którym chcemy wkleić obraz użytkownika (np. pierwsze pole)
        int xOffset = fieldWidth; // Pozycja w poziomie
        int yOffset = fieldHeight; // Pozycja w pionie

        // Tworzymy kopię obrazu szachownicy, aby móc na nim dokonać zmian
        Mat outputImage = chessboardImage.clone();

        // Kopiowanie obrazu użytkownika na wybrane pole szachownicy
        for (int y = 0; y < resizedUserImage.rows(); y++) {
            for (int x = 0; x < resizedUserImage.cols(); x++) {
                // Pobranie pikseli z obrazu użytkownika
                double[] userPixel = resizedUserImage.get(y, x);

                // Kopiowanie pikseli do odpowiedniej pozycji na szachownicy
                outputImage.put(y + yOffset, x + xOffset, userPixel);
            }
        }

        // Zapisanie wyniku
        String outputPath = "final_chessboard_image.jpg";
        Imgcodecs.imwrite(outputPath, outputImage);
        System.out.println("Image with user image inserted saved as: " + outputPath);
    }


    void algorytm(int userOption)
    {

        // wczytywanie obrazów
        Mat backgorund = Imgcodecs.imread("C:\\Users\\doria\\IdeaProjectsp\\PrzetwarzanieObrazow_7_GreenScreen\\src\\background.jpg");
        Mat selfie = Imgcodecs.imread("C:\\Users\\doria\\IdeaProjectsp\\PrzetwarzanieObrazow_7_GreenScreen\\src\\selfie.jpg");

        // dopasowanie rozmiarów
        Imgproc.resize(backgorund, backgorund, new Size(selfie.cols(), selfie.rows()));

        // konwersja selfie do HSV
        Mat selfieHSV = new Mat();
        Imgproc.cvtColor(selfie, selfieHSV, Imgproc.COLOR_RGB2HSV);


        // tworzenie maski
        Scalar lowerGreen = new Scalar(35, 55, 55);
        Scalar upperGreen = new Scalar(85, 255, 255);
        Mat mask = new Mat();
        Core.inRange(selfieHSV, lowerGreen, upperGreen, mask);

        // zastosowanie wybranego filtra

        switch (userOption) {
            case 1:
                Imgproc.GaussianBlur(mask, mask, new Size(5,5), 0);
                break;
            case 2:
                Imgproc.medianBlur(mask,mask,3);
                break;
            case 3:
                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
                break;
            default:
                System.out.println("Nieprawidlowy wybor filtra!");
                break;
        }


        Mat maskInv = new Mat();
        Core.bitwise_not(mask, maskInv);

        Mat selfieFG = new Mat();
        Core.bitwise_and(selfie, selfie, selfieFG, maskInv);

        Mat backgroundBG = new Mat();
        Core.bitwise_and(backgorund, backgorund, backgroundBG, mask);

        Mat result = new Mat();
        Core.add(selfieFG, backgroundBG, result);
        saveImage(result, "algorithmResult.jpg");
        applyImageOnChessboard("C:\\Users\\doria\\IdeaProjectsp\\PrzetwarzanieObrazow_7_GreenScreen\\src\\szablon-ramki-posta-na-instagramie_1393-59.png","C:\\Users\\doria\\IdeaProjectsp\\PrzetwarzanieObrazow_7_GreenScreen\\src\\algorithmResult.jpg" );
    }

}
