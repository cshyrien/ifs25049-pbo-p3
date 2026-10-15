package framework.view;


import adapter.presenter.GuestPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.GuestUseCase;


/**
 * Tampilan konsol aplikasi buku tamu.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis — hanya menangani interaksi user.
 */
public class GuestView {
    /** Use case yang menjalankan operasi bisnis. */
    private final GuestUseCase guestUseCase;


    /** Presenter yang memformat hasil ke output layar. */
    private final GuestPresenter presenter;


    public GuestView(GuestUseCase guestUseCase, GuestPresenter presenter) {
        this.guestUseCase = guestUseCase;
        this.presenter = presenter;
    }


    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar tamu terkini sebelum menu
            presenter.showGuests(guestUseCase.getAllGuests());
            printMenu();

            String input = InputUtil.input("Pilih").trim();
            System.out.println();
            switch (input) {
                case "1" -> registerGuest();
                case "2" -> searchGuest();
                case "3" -> removeGuest();
                case "x" -> running = false;
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
        System.out.println("1. Daftarkan");
        System.out.println("2. Cari");
        System.out.println("3. Hapus");
        System.out.println("x. Keluar");
    }


    /** Form pendaftaran tamu baru. */
    private void registerGuest() {
        System.out.println("[Mendaftarkan Tamu]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.trim().equals("x")) {
            return;
        }

        String purpose = InputUtil.input("Tujuan Kunjungan (x Jika Batal)");
        if (purpose.trim().equals("x")) {
            return;
        }

        try {
            presenter.showRegisterSuccess(guestUseCase.registerGuest(name, purpose));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }


    /** Form cari tamu berdasarkan nama. */
    private void searchGuest() {
        System.out.println("[Mencari Tamu]");
        String keyword = InputUtil.input("Nama (x Jika Batal)");

        if (!keyword.trim().equals("x")) {
            presenter.showSearchResults(guestUseCase.searchGuests(keyword), keyword);
        }
    }


    /** Form hapus tamu berdasarkan ID. */
    private void removeGuest() {
        System.out.println("[Menghapus Tamu]");
        String strId = InputUtil.input("[ID Tamu] yang dihapus (x Jika Batal)");

        if (strId.trim().equals("x")) {
            return;
        }

        try {
            int id = parseId(strId);
            guestUseCase.removeGuest(id);
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
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("ID tidak valid!");
        }
    }
}
