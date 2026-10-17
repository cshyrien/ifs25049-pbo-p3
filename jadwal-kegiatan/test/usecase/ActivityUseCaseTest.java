package usecase;


import adapter.repository.ActivityRepository;
import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;


/**
 * Unit test sederhana (tanpa framework eksternal) untuk ActivityUseCase.
 *
 * Jalankan dari folder studi kasus:
 *   javac -encoding UTF-8 -d output -sourcepath src;test test/usecase/ActivityUseCaseTest.java
 *   java -cp output usecase.ActivityUseCaseTest
 * (di Linux/macOS pakai ':' sebagai pengganti ';' pada -sourcepath)
 */
public class ActivityUseCaseTest {

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
        ActivityUseCase useCase = new ActivityUseCase(new ActivityRepository());

        Activity a = useCase.addActivity(" Kuliah OOP ", "Senin", "08:00");
        check("tambah kegiatan valid", a.getId() == 1 && a.getTitle().equals("Kuliah OOP"));
        useCase.addActivity("Praktikum", "Rabu", "13:00");

        expectThrows("judul kosong ditolak", ValidationException.class, () -> useCase.addActivity("", "Senin", "08:00"));
        expectThrows("hari kosong ditolak", ValidationException.class, () -> useCase.addActivity("X", " ", "08:00"));
        expectThrows("waktu kosong ditolak", ValidationException.class, () -> useCase.addActivity("X", "Senin", ""));
        check("data ditolak tidak tersimpan", useCase.getAllActivities().size() == 2);

        useCase.updateActivity(2, null, "Selasa", null);
        Activity updated = useCase.getAllActivities().get(1);
        check("update parsial hanya mengubah hari", updated.getDay().equals("Selasa")
                && updated.getTitle().equals("Praktikum") && updated.getTime().equals("13:00"));
        check("withDay tidak mengubah objek asli", a.withDay("Jumat").getDay().equals("Jumat") && a.getDay().equals("Senin"));
        expectThrows("update ID tidak ada", EntityNotFoundException.class, () -> useCase.updateActivity(9, "X", null, null));
        expectThrows("update dengan nilai kosong ditolak", ValidationException.class, () -> useCase.updateActivity(1, " ", null, null));

        check("pencarian judul", useCase.searchActivities("kuliah").size() == 1);
        check("urut hari Senin -> Minggu", useCase.sortActivities(SortOption.DAY).get(0).getDay().equals("Senin"));
        check("urut judul Z-A", useCase.sortActivities(SortOption.TITLE_DESC).get(0).getTitle().equals("Praktikum"));

        useCase.removeActivity(1);
        check("hapus kegiatan berhasil", useCase.getAllActivities().size() == 1);
        expectThrows("hapus ID tidak ada", EntityNotFoundException.class, () -> useCase.removeActivity(1));
        finish();
    }
}
