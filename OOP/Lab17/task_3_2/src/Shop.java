import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Магазин — колекція товарів. Надає два різні ітератори:
 *  - byName(): за алфавітним порядком назв;
 *  - byQuantityDesc(): за спаданням кількості.
 * Сам Shop теж Iterable (за замовчуванням — за назвою).
 */
public class Shop implements Iterable<ShopItem> {

    private final HashMap<String, ShopItem> items = new HashMap<>();

    public void putItem(ShopItem item) {
        items.put(item.getName(), item);
    }

    /** Ітератор за алфавітним порядком назв. */
    public Iterable<ShopItem> byName() {
        return () -> sortedIterator(Comparator.comparing(ShopItem::getName));
    }

    /** Ітератор за спаданням кількості. */
    public Iterable<ShopItem> byQuantityDesc() {
        return () -> sortedIterator(Comparator.comparingInt(ShopItem::getQuantity).reversed());
    }

    @Override
    public Iterator<ShopItem> iterator() {
        return sortedIterator(Comparator.comparing(ShopItem::getName));
    }

    /** Спільна логіка: повертає ітератор по копії, відсортованій компаратором. */
    private Iterator<ShopItem> sortedIterator(Comparator<ShopItem> comparator) {
        List<ShopItem> list = new ArrayList<>(items.values());
        list.sort(comparator);
        return list.iterator();
    }
}
