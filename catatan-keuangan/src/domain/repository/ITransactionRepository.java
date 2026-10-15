package domain.repository;


import domain.entity.Transaction;
import domain.entity.TransactionType;


/**
 * Port spesifik Transaction yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan transaksi baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /**
     * Menyimpan transaksi baru; repository bertanggung jawab membuat ID unik.
     *
     * @param description keterangan transaksi
     * @param amount      jumlah uang
     * @param type        jenis transaksi (pemasukan/pengeluaran)
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction save(String description, long amount, TransactionType type);
}
