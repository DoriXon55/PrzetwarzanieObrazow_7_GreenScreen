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


    public static List<Rect> detectChessboardCells(Mat image) {
        // Konwertuj obraz na skalę szarości
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Zastosowanie detekcji krawędzi Canny
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 100, 300);

        // Znalezienie konturów
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Wyszukiwanie prostokątnych konturów, które odpowiadają polom szachownicy
        List<Rect> chessboardCells = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            // Załóżmy, że pole szachownicy ma odpowiednią wielkość
            if (boundingRect.width > 30 && boundingRect.height > 30) {
                chessboardCells.add(boundingRect);
            }
        }

        return chessboardCells;
    }

    // Funkcja do wklejania obrazu użytkownika na szachownicę
    public static void applyUserImageOnChessboard(Mat chessboardImage, Mat userImage, List<Rect> chessboardCells) {
        // Zakładając, że pole szachownicy ma odpowiedni rozmiar
        for (Rect cell : chessboardCells) {
            // Dopasowanie rozmiaru obrazu użytkownika do rozmiaru pola szachownicy
            Mat resizedUserImage = new Mat();
            Imgproc.resize(userImage, resizedUserImage, new Size(cell.width, cell.height));

            // Przekopiowanie obrazu użytkownika do pola szachownicy
            for (int y = 0; y < resizedUserImage.rows(); y++) {
                for (int x = 0; x < resizedUserImage.cols(); x++) {
                    double[] userPixel = resizedUserImage.get(y, x);
                    chessboardImage.put(cell.y + y, cell.x + x, userPixel);
                }
            }
        }

        // Zapisz wynikowy obraz
        String outputPath = "final_chessboard_image_with_overlay.jpg";
        Imgcodecs.imwrite(outputPath, chessboardImage);
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
