package domain.entity;


/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan maupun penyimpanan. Bersifat immutable: seluruh field final,
 * sehingga data transaksi yang sudah dicatat tidak dapat diubah dari luar.
 */
public class Transaction {
    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;


    /** Keterangan transaksi. */
    private final String description;


    /** Jumlah uang (selalu bernilai positif). */
    private final long amount;


    /** Jenis transaksi: pemasukan atau pengeluaran. */
    private final TransactionType type;


    public Transaction(int id, String description, long amount, TransactionType type) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }


    public int getId() {
        return id;
    }


    public String getDescription() {
        return description;
    }


    public long getAmount() {
        return amount;
    }


    public TransactionType getType() {
        return type;
    }


    /** Nilai transaksi terhadap saldo: positif untuk pemasukan, negatif untuk pengeluaran. */
    public long signedAmount() {
        return type == TransactionType.INCOME ? amount : -amount;
    }

}
