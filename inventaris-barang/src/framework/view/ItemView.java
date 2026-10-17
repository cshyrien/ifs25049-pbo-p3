package framework.view;


import adapter.presenter.ItemPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.ItemUseCase;


/**
 * Tampilan konsol aplikasi inventaris barang.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis — hanya menangani interaksi user.
 */
public class ItemView {
    /** Use case yang menjalankan operasi bisnis. */
    private final ItemUseCase itemUseCase;


    /** Presenter yang memformat hasil ke output layar. */
    private final ItemPresenter presenter;


    public ItemView(ItemUseCase itemUseCase, ItemPresenter presenter) {
        this.itemUseCase = itemUseCase;
        this.presenter = presenter;
    }


    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar barang terkini sebelum menu
            presenter.showItems(itemUseCase.getAllItems());
            printMenu();

            String input = InputUtil.input("Pilih").trim();
            System.out.println();
            switch (input) {
                case "1" -> addItem();
                case "2" -> updateStock();
                case "3" -> searchItem();
                case "4" -> sortItem();
                case "5" -> removeItem();
                case InputUtil.CANCEL_KEY -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }


    /** Mencetak opsi menu ke layar. */
    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Tambah");
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }


    /** Form tambah barang baru. */
    private void addItem() {
        System.out.println("[Menambah Barang]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (InputUtil.isCancel(name)) {
            return;
        }

        String strQuantity = InputUtil.input("Jumlah");
        if (InputUtil.isCancel(strQuantity)) {
            return;
        }

        int quantity;
        try {
            quantity = parseQuantity(strQuantity);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        String category = InputUtil.input("Kategori (x Jika Batal)");
        if (InputUtil.isCancel(category)) {
            return;
        }

        try {
            presenter.showAddSuccess(itemUseCase.addItem(name, quantity, category));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }


    /** Form ubah stok barang berdasarkan ID. */
    private void updateStock() {
        System.out.println("[Mengubah Stok]");
        String strId = InputUtil.input("ID Barang yang diubah (x Jika Batal)");
        if (InputUtil.isCancel(strId)) {
            return;
        }

        int id;
        try {
            id = parseId(strId);
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
            return;
        }

        String strQuantity = InputUtil.input("Jumlah Baru (Kosongkan jika tidak ingin mengubah)");
        if (InputUtil.isCancel(strQuantity)) {
            return;
        }

        try {
            // Kosong = stok tidak diubah (null), selain itu harus angka valid
            Integer quantity = strQuantity.isBlank() ? null : parseQuantity(strQuantity);
            itemUseCase.updateStock(id, quantity);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }


    /** Form cari barang berdasarkan nama. */
    private void searchItem() {
        System.out.println("[Mencari Barang]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(itemUseCase.searchItems(keyword), keyword);
        }
    }


    /** Form urutkan barang berdasarkan pilihan user. */
    private void sortItem() {
        System.out.println("[Mengurutkan Barang]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("3. Jumlah (Terkecil -> Terbesar)");
        System.out.println("4. Jumlah (Terbesar -> Terkecil)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih").trim();
        System.out.println();
        if (InputUtil.isCancel(input)) {
            return;
        }

        // Konversi input angka ke enum domain
        SortOption option = mapSortOption(input);
        if (option == null) {
            presenter.showInvalidSortOption();
            return;
        }

        presenter.showSortedItems(itemUseCase.sortItems(option));
    }


    /** Form hapus barang berdasarkan ID. */
    private void removeItem() {
        System.out.println("[Menghapus Barang]");
        String strId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = parseId(strId);
            itemUseCase.removeItem(id);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }


    /**
     * Mengonversi input string menjadi ID.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private int parseId(String value) throws ValidationException {
        return InputUtil.parseInt(value, "ID tidak valid!");
    }


    /**
     * Mengonversi input string menjadi jumlah stok.
     *
     * @throws ValidationException jika string bukan format angka
     */
    private int parseQuantity(String value) throws ValidationException {
        return InputUtil.parseInt(value, "Jumlah stok tidak valid!");
    }


    /** Memetakan pilihan menu (1-4) ke {@link SortOption} domain. */
    private SortOption mapSortOption(String input) {
        return switch (input) {
            case "1" -> SortOption.NAME_ASC;
            case "2" -> SortOption.NAME_DESC;
            case "3" -> SortOption.QUANTITY_ASC;
            case "4" -> SortOption.QUANTITY_DESC;
            default -> null;
        };
    }
}
