package adapter.repository;


import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.repository.ITransactionRepository;


/**
 * Implementasi repository transaksi menggunakan basis generic {@link InMemoryRepository}.
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {
    /** Penghitung ID otomatis, bertambah setiap kali transaksi baru disimpan. */
    private int idCounter = 0;


    public TransactionRepository() {
        super(Transaction::getId);
    }


    @Override
    public Transaction save(String description, long amount, TransactionType type) {
        Transaction transaction = new Transaction(nextId(), description, amount, type);
        return save(transaction);
    }


    /** Menghasilkan ID unik berikutnya. */
    private int nextId() {
        return ++idCounter;
    }
}
