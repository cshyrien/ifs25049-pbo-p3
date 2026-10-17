package usecase;


import adapter.repository.ContactRepository;
import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import java.util.List;


/**
 * Unit test sederhana (tanpa framework eksternal) untuk ContactUseCase.
 *
 * Jalankan dari folder studi kasus:
 *   javac -encoding UTF-8 -d output -sourcepath src;test test/usecase/ContactUseCaseTest.java
 *   java -cp output usecase.ContactUseCaseTest
 * (di Linux/macOS pakai ':' sebagai pengganti ';' pada -sourcepath)
 */
public class ContactUseCaseTest {

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
        ContactUseCase useCase = new ContactUseCase(new ContactRepository());

        Contact budi = useCase.addContact("  Budi ", "081234567890", "budi@email.com");
        check("tambah kontak valid memberi ID 1 dan menghapus spasi", budi.getId() == 1 && budi.getName().equals("Budi"));
        useCase.addContact("Ani", "082345678901", "ani@email.com");

        expectThrows("nama kosong ditolak", ValidationException.class, () -> useCase.addContact(" ", "081234567890", "x@y.com"));
        expectThrows("telepon kosong ditolak", ValidationException.class, () -> useCase.addContact("Citra", "", "x@y.com"));
        expectThrows("email kosong ditolak", ValidationException.class, () -> useCase.addContact("Citra", "081234567890", ""));
        expectThrows("telepon berisi huruf ditolak", ValidationException.class, () -> useCase.addContact("Citra", "08abc", "x@y.com"));
        expectThrows("email tanpa @ ditolak", ValidationException.class, () -> useCase.addContact("Citra", "081234567890", "citra.email.com"));
        check("kontak yang ditolak tidak tersimpan", useCase.getAllContacts().size() == 2);

        // Update parsial pada entity immutable: objek lama tidak berubah, repository memegang objek baru
        useCase.updateContact(1, "Budi Baru", null, null);
        Contact updated = useCase.getAllContacts().get(0);
        check("update parsial hanya mengubah nama", updated.getName().equals("Budi Baru")
                && updated.getPhone().equals("081234567890") && updated.getEmail().equals("budi@email.com"));
        check("objek lama tidak berubah (immutable)", budi.getName().equals("Budi") && updated != budi);
        check("withPhone mengembalikan objek baru", budi.withPhone("0899999999").getPhone().equals("0899999999")
                && budi.getPhone().equals("081234567890"));

        expectThrows("update dengan telepon tidak valid ditolak", ValidationException.class, () -> useCase.updateContact(1, null, "abc", null));
        check("update gagal tidak mengubah data", useCase.getAllContacts().get(0).getPhone().equals("081234567890"));
        expectThrows("update ID tidak ada", EntityNotFoundException.class, () -> useCase.updateContact(99, "X", null, null));

        List<Contact> found = useCase.searchContacts("ANI");
        check("pencarian case-insensitive", found.size() == 1 && found.get(0).getName().equals("Ani"));
        check("urut A-Z", useCase.sortContacts(SortOption.NAME_ASC).get(0).getName().equals("Ani"));
        check("urut Z-A", useCase.sortContacts(SortOption.NAME_DESC).get(0).getName().equals("Budi Baru"));

        useCase.removeContact(2);
        check("hapus kontak berhasil", useCase.getAllContacts().size() == 1);
        expectThrows("hapus ID tidak ada", EntityNotFoundException.class, () -> useCase.removeContact(2));
        finish();
    }
}
