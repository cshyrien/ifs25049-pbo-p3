package framework.view;


import adapter.presenter.ActivityPresenter;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import usecase.ActivityUseCase;


/**
 * Tampilan konsol aplikasi jadwal kegiatan.
 * Menerima input user, memanggil use case, dan menangani exception domain secara terstruktur.
 * Layer ini tidak mengandung logika bisnis — hanya menangani interaksi user.
 */
public class ActivityView {
    /** Use case yang menjalankan operasi bisnis. */
    private final ActivityUseCase activityUseCase;


    /** Presenter yang memformat hasil ke output layar. */
    private final ActivityPresenter presenter;


    public ActivityView(ActivityUseCase activityUseCase, ActivityPresenter presenter) {
        this.activityUseCase = activityUseCase;
        this.presenter = presenter;
    }


    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            // Tampilkan daftar kegiatan terkini sebelum menu
            presenter.showActivities(activityUseCase.getAllActivities());
            printMenu();

            String input = InputUtil.input("Pilih").trim();
            System.out.println();
            switch (input) {
                case "1" -> addActivity();
                case "2" -> updateActivity();
                case "3" -> searchActivity();
                case "4" -> sortActivity();
                case "5" -> removeActivity();
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
        System.out.println("2. Ubah");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }


    /** Form tambah kegiatan baru. */
    private void addActivity() {
        System.out.println("[Menambah Kegiatan]");
        String title = InputUtil.input("Judul (x Jika Batal)");
        if (InputUtil.isCancel(title)) {
            return;
        }

        String day = InputUtil.input("Hari (x Jika Batal)");
        if (InputUtil.isCancel(day)) {
            return;
        }

        String time = InputUtil.input("Waktu (x Jika Batal)");
        if (InputUtil.isCancel(time)) {
            return;
        }

        try {
            presenter.showAddSuccess(activityUseCase.addActivity(title, day, time));
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }


    /** Form ubah judul, hari, dan/atau waktu kegiatan (parsial). */
    private void updateActivity() {
        System.out.println("[Mengubah Kegiatan]");
        String strId = InputUtil.input("ID Kegiatan yang diubah (x Jika Batal)");
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

        String newTitle = InputUtil.input("Judul Baru (Kosongkan jika tidak ingin mengubah, x Jika Batal)");
        if (InputUtil.isCancel(newTitle)) {
            return;
        }

        String newDay = InputUtil.input("Hari Baru (Kosongkan jika tidak ingin mengubah, x Jika Batal)");
        if (InputUtil.isCancel(newDay)) {
            return;
        }

        String newTime = InputUtil.input("Waktu Baru (Kosongkan jika tidak ingin mengubah, x Jika Batal)");
        if (InputUtil.isCancel(newTime)) {
            return;
        }

        // null berarti field tersebut tidak diubah
        String title = newTitle.isBlank() ? null : newTitle;
        String day = newDay.isBlank() ? null : newDay;
        String time = newTime.isBlank() ? null : newTime;

        try {
            activityUseCase.updateActivity(id, title, day, time);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showError(e.getMessage());
        }
    }


    /** Form cari kegiatan berdasarkan judul. */
    private void searchActivity() {
        System.out.println("[Mencari Kegiatan]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");

        if (!InputUtil.isCancel(keyword)) {
            presenter.showSearchResults(activityUseCase.searchActivities(keyword), keyword);
        }
    }


    /** Form urutkan kegiatan berdasarkan pilihan user. */
    private void sortActivity() {
        System.out.println("[Mengurutkan Kegiatan]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Hari (Senin -> Minggu)");
        System.out.println("2. Waktu (Awal -> Akhir)");
        System.out.println("3. Judul (A-Z)");
        System.out.println("4. Judul (Z-A)");
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

        presenter.showSortedActivities(activityUseCase.sortActivities(option));
    }


    /** Form hapus kegiatan berdasarkan ID. */
    private void removeActivity() {
        System.out.println("[Menghapus Kegiatan]");
        String strId = InputUtil.input("[ID Kegiatan] yang dihapus (x Jika Batal)");

        if (InputUtil.isCancel(strId)) {
            return;
        }

        try {
            int id = parseId(strId);
            activityUseCase.removeActivity(id);
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


    /** Memetakan pilihan menu (1-4) ke {@link SortOption} domain. */
    private SortOption mapSortOption(String input) {
        return switch (input) {
            case "1" -> SortOption.DAY;
            case "2" -> SortOption.TIME;
            case "3" -> SortOption.TITLE_ASC;
            case "4" -> SortOption.TITLE_DESC;
            default -> null;
        };
    }
}
