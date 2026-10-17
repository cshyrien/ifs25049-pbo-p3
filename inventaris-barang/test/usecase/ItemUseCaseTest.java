package usecase;


import adapter.repository.ItemRepository;
import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;


/**
 * Unit test sederhana (tanpa framework eksternal) untuk ItemUseCase.
 *
 * Jalankan dari folder studi kasus:
 *   javac -encoding UTF-8 -d output -sourcepath src;test test/usecase/ItemUseCaseTest.java
 *   java -cp output usecase.ItemUseCaseTest
 * (di Linux/macOS pakai ':' sebagai pengganti ';' pada -sourcepath)
 */
public class ItemUseCaseTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    /** Memastikan blok kode melempar exception bertipe tertentu. */
    private static void expectThrows(String name, Class<? extends Exception> type, ThrowingRunnable action) {
        try {
            action.run();
            check(name, false);
        } catch (Exception e) {
            check(name, type.isInstance(e));
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void finish() {
        System.out.printf("%nHasil: %d lulus, %d gagal%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        ItemUseCase useCase = new ItemUseCase(new ItemRepository());

        Item mouse = useCase.addItem(" Mouse ", 10, "Elektronik");
        check("tambah barang valid", mouse.getId() == 1 && mouse.getName().equals("Mouse") && mouse.getQuantity() == 10);
        useCase.addItem("Keyboard", 5, "Elektronik");

        expectThrows("nama kosong ditolak", ValidationException.class, () -> useCase.addItem("", 1, "A"));
        expectThrows("stok nol ditolak", ValidationException.class, () -> useCase.addItem("X", 0, "A"));
        expectThrows("stok negatif ditolak", ValidationException.class, () -> useCase.addItem("X", -3, "A"));
        expectThrows("kategori kosong ditolak", ValidationException.class, () -> useCase.addItem("X", 1, " "));
        check("data ditolak tidak tersimpan", useCase.getAllItems().size() == 2);

        useCase.updateStock(1, 25);
        check("stok berubah di repository", useCase.getAllItems().get(0).getQuantity() == 25);
        check("objek lama tidak berubah (immutable)", mouse.getQuantity() == 10);
        useCase.updateStock(1, null);
        check("stok null berarti tidak diubah", useCase.getAllItems().get(0).getQuantity() == 25);
        expectThrows("stok baru tidak valid ditolak", ValidationException.class, () -> useCase.updateStock(1, 0));
        expectThrows("update ID tidak ada", EntityNotFoundException.class, () -> useCase.updateStock(9, 1));

        check("pencarian nama", useCase.searchItems("KEY").size() == 1);
        check("urut stok terkecil dulu", useCase.sortItems(SortOption.QUANTITY_ASC).get(0).getName().equals("Keyboard"));

        useCase.removeItem(2);
        check("hapus barang berhasil", useCase.getAllItems().size() == 1);
        expectThrows("hapus ID tidak ada", EntityNotFoundException.class, () -> useCase.removeItem(2));
        finish();
    }
}
