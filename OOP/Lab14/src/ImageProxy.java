/**
 * Замісник (Virtual Proxy) для ImageFile.
 * Зберігає лише шлях до зображення і НЕ завантажує його з диску,
 * доки не буде викликано display() уперше (відкладене завантаження).
 * Реалізує той самий інтерфейс DisplayObject, тож для клієнта прозорий.
 */
public class ImageProxy implements DisplayObject {

    private final String path;
    private ImageFile realImage;   // створюється лише за потреби (lazy)

    public ImageProxy(String path) {
        this.path = path;          // важке завантаження тут НЕ відбувається
    }

    @Override
    public void display() {
        if (realImage == null) {
            // реальний об'єкт (і завантаження файлу) створюється
            // тільки в момент першого показу
            realImage = new ImageFile(path);
        }
        realImage.display();
    }
}
