package adapter.repository;


import domain.entity.Contact;
import domain.repository.IContactRepository;


/**
 * Implementasi repository kontak menggunakan basis generic {@link InMemoryRepository}.
 */
public class ContactRepository extends InMemoryRepository<Contact, Integer> implements IContactRepository {
    /** Penghitung ID otomatis, bertambah setiap kali kontak baru disimpan. */
    private int idCounter = 0;


    public ContactRepository() {
        super(Contact::getId);
    }


    @Override
    public Contact save(String name, String phone, String email) {
        Contact contact = new Contact(nextId(), name, phone, email);
        return save(contact);
    }


    /** Menghasilkan ID unik berikutnya. */
    private int nextId() {
        return ++idCounter;
    }
}
