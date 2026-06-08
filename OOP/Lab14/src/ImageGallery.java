public class ImageGallery {

    public static void main(String[] args) {
        // якщо виникає помилка завантаження, замініть відносний шлях на абсолютний!
        String path = "resources/";

        // Створюємо ЗАМІСНИКІВ — жоден файл ще не читається з диску.
        DisplayObject[] images = {
                new ImageProxy(path + "image1.jpeg"),
                new ImageProxy(path + "image2.jpeg"),
                new ImageProxy(path + "image3.jpeg"),
                new ImageProxy(path + "image4.jpeg"),
                new ImageProxy(path + "image5.jpeg"),
                new ImageProxy(path + "image6.jpeg"),
                new ImageProxy(path + "image7.jpeg"),
                new ImageProxy(path + "image8.jpeg"),
                new ImageProxy(path + "image9.jpeg"),
                new ImageProxy(path + "image10.jpeg"),
        };

        System.out.println("Gallery is ready. No image has been loaded yet.\n");

        // Завантаження відбувається лише в момент показу кожного зображення.
        for (DisplayObject image : images) {
            image.display();
        }
    }
}
