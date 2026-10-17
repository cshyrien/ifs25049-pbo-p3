package usecase;


import adapter.repository.TransactionRepository;
import domain.entity.SortOption;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;


/**
 * Unit test sederhana (tanpa framework eksternal) untuk FinanceUseCase.
 *
 * Jalankan dari folder studi kasus:
 *   javac -encoding UTF-8 -d output -sourcepath src;test test/usecase/FinanceUseCaseTest.java
 *   java -cp output usecase.FinanceUseCaseTest
 * (di Linux/macOS pakai ':' sebagai pengganti ';' pada -sourcepath)
 */
public class FinanceUseCaseTest {

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
        FinanceUseCase useCase = new FinanceUseCase(new TransactionRepository());

        useCase.addTransaction(" Gaji ", 5000000, TransactionType.INCOME);
        useCase.addTransaction("Makan", 50000, TransactionType.EXPENSE);
        check("dua transaksi tersimpan", useCase.getAllTransactions().size() == 2);
        check("saldo = pemasukan - pengeluaran", useCase.getBalance() == 4950000);

        expectThrows("keterangan kosong ditolak", ValidationException.class, () -> useCase.addTransaction("", 100, TransactionType.INCOME));
        expectThrows("jumlah nol ditolak", ValidationException.class, () -> useCase.addTransaction("X", 0, TransactionType.INCOME));
        expectThrows("jumlah negatif ditolak", ValidationException.class, () -> useCase.addTransaction("X", -5, TransactionType.EXPENSE));
        expectThrows("tipe null ditolak", ValidationException.class, () -> useCase.addTransaction("X", 100, null));
        check("data ditolak tidak mengubah saldo", useCase.getBalance() == 4950000);

        check("pencarian keterangan", useCase.searchTransactions("MAK").size() == 1);
        check("urut jumlah terbesar dulu", useCase.sortTransactions(SortOption.AMOUNT_DESC).get(0).getAmount() == 5000000);

        useCase.removeTransaction(2);
        check("hapus transaksi mengubah saldo", useCase.getBalance() == 5000000);
        expectThrows("hapus ID tidak ada", EntityNotFoundException.class, () -> useCase.removeTransaction(2));
        finish();
    }
}
