package usecase;


import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;


/**
 * Use case yang menangani logika bisnis aplikasi inventaris barang.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ItemUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IItemRepository itemRepository;


    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    /** Mengambil semua barang yang tersedia. */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }


    /** Menambahkan barang baru setelah validasi nama, stok, dan kategori. */
    public Item addItem(String name, int quantity, String category) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama barang tidak boleh kosong!");
        }
        validateQuantity(quantity);
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("Kategori barang tidak boleh kosong!");
        }
        return itemRepository.save(name.trim(), quantity, category.trim());
    }


    public void updateStock(int id, Integer quantity) throws EntityNotFoundException, ValidationException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Barang", id));

        // quantity null berarti stok tidak diubah (update parsial)
        if (quantity != null) {
            validateQuantity(quantity);
            item.changeQuantity(quantity);
        }
        itemRepository.update(item);
    }


    /** Menghapus barang berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeItem(int id) throws EntityNotFoundException {
        boolean deleted = itemRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Barang", id);
        }
    }


    /** Mencari barang yang namanya mengandung kata kunci (case-insensitive). */
    public List<Item> searchItems(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return itemRepository.findBy(item -> item.getName().toLowerCase().contains(lowerKeyword));
    }


    /** Mengurutkan barang sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Item> sortItems(SortOption option) {
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }


    /** Aturan validasi stok: harus lebih besar dari nol. */
    private void validateQuantity(int quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Jumlah stok tidak valid!");
        }
    }
}
