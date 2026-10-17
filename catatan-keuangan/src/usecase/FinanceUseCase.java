package usecase;


import domain.entity.SortOption;
import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.ITransactionRepository;
import java.util.List;


/**
 * Use case yang menangani logika bisnis aplikasi catatan keuangan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;


    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    /** Mengambil semua transaksi yang tersedia. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


    /** Menambahkan transaksi baru setelah validasi keterangan, jumlah, dan tipe. */
    public Transaction addTransaction(String description, long amount, TransactionType type)
            throws ValidationException {
        validateNotBlank(description, "Keterangan tidak boleh kosong!");
        if (amount <= 0) {
            throw new ValidationException("Jumlah tidak valid!");
        }
        if (type == null) {
            throw new ValidationException("Tipe transaksi tidak valid!");
        }
        return transactionRepository.save(description.trim(), amount, type);
    }


    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException {
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Transaksi", id);
        }
    }


    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }


    /** Mengurutkan transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) {
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }


    /** Menghitung saldo: total pemasukan dikurangi total pengeluaran. */
    public long getBalance() {
        long balance = 0;
        for (Transaction transaction : transactionRepository.findAll()) {
            if (transaction.getType() == TransactionType.INCOME) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }

    /** Aturan validasi: teks tidak boleh null atau kosong. */
    private void validateNotBlank(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
