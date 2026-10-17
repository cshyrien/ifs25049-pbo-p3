package usecase;


import adapter.repository.GuestRepository;
import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;


/**
 * Unit test sederhana (tanpa framework eksternal) untuk GuestUseCase.
 *
 * Jalankan dari folder studi kasus:
 *   javac -encoding UTF-8 -d output -sourcepath src;test test/usecase/GuestUseCaseTest.java
 *   java -cp output usecase.GuestUseCaseTest
 * (di Linux/macOS pakai ':' sebagai pengganti ';' pada -sourcepath)
 */
public class GuestUseCaseTest {

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
        GuestUseCase useCase = new GuestUseCase(new GuestRepository());

        Guest budi = useCase.registerGuest(" Budi ", "Rapat");
        check("daftar tamu valid", budi.getId() == 1 && budi.getName().equals("Budi"));
        useCase.registerGuest("Ani", "Kunjungan");

        expectThrows("nama kosong ditolak", ValidationException.class, () -> useCase.registerGuest("", "Rapat"));
        expectThrows("tujuan kosong ditolak", ValidationException.class, () -> useCase.registerGuest("Citra", " "));
        check("data ditolak tidak tersimpan", useCase.getAllGuests().size() == 2);

        check("pencarian nama case-insensitive", useCase.searchGuests("bud").size() == 1);
        check("kata kunci tidak cocok -> kosong", useCase.searchGuests("zzz").isEmpty());

        useCase.removeGuest(1);
        check("hapus tamu berhasil", useCase.getAllGuests().size() == 1);
        expectThrows("hapus ID tidak ada", EntityNotFoundException.class, () -> useCase.removeGuest(1));
        finish();
    }
}
