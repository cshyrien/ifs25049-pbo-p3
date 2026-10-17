package domain.entity;


/**
 * Entity inti yang merepresentasikan satu barang inventaris.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan maupun penyimpanan. Bersifat immutable: seluruh field final,
 * dan perubahan dilakukan lewat method with* yang mengembalikan objek baru.
 */
public class Item {
    /** ID unik, tidak berubah setelah dibuat. */
    private final int id;


    /** Nama barang. */
    private final String name;


    /** Jumlah stok barang. */
    private final int quantity;


    /** Kategori barang. */
    private final String category;


    public Item(int id, String name, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }


    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public int getQuantity() {
        return quantity;
    }


    public String getCategory() {
        return category;
    }


    /** Mengembalikan salinan baru dengan nama barang yang diganti; objek ini tidak berubah. */
    public Item withName(String newName) {
        return new Item(id, newName, quantity, category);
    }


    /** Mengembalikan salinan baru dengan jumlah stok barang yang diganti; objek ini tidak berubah. */
    public Item withQuantity(int newQuantity) {
        return new Item(id, name, newQuantity, category);
    }


    /** Mengembalikan salinan baru dengan kategori barang yang diganti; objek ini tidak berubah. */
    public Item withCategory(String newCategory) {
        return new Item(id, name, quantity, newCategory);
    }
}
