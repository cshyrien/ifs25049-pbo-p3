package domain.entity;


/**
 * Jenis transaksi keuangan: pemasukan atau pengeluaran.
 * Urutan deklarasi dipakai sebagai urutan natural saat pengurutan berdasarkan tipe.
 */
public enum TransactionType {
    /** Uang masuk (menambah saldo). */
    INCOME,

    /** Uang keluar (mengurangi saldo). */
    EXPENSE
}
