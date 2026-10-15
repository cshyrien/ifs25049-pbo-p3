package usecase;


import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;


/**
 * Use case yang menangani logika bisnis aplikasi kontak teman.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ContactUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IContactRepository contactRepository;


    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }


    /** Mengambil semua kontak yang tersedia. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }


    /** Menambahkan kontak baru setelah validasi nama, telepon, dan email. */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        validateNotBlank(name, "Nama kontak tidak boleh kosong!");
        validateNotBlank(phone, "Nomor telepon tidak boleh kosong!");
        validateNotBlank(email, "Email tidak boleh kosong!");
        return contactRepository.save(name.trim(), phone.trim(), email.trim());
    }


    /**
     * Mengubah nama, telepon, dan/atau email kontak.
     * Parameter {@code null} berarti field tersebut tidak diubah (update parsial).
     */
    public void updateContact(int id, String name, String phone, String email)
            throws EntityNotFoundException, ValidationException {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kontak", id));

        // Validasi seluruh field yang diubah sebelum ada perubahan yang diterapkan
        if (name != null) {
            validateNotBlank(name, "Nama kontak tidak boleh kosong!");
        }
        if (phone != null) {
            validateNotBlank(phone, "Nomor telepon tidak boleh kosong!");
        }
        if (email != null) {
            validateNotBlank(email, "Email tidak boleh kosong!");
        }

        if (name != null) {
            contact.changeName(name.trim());
        }
        if (phone != null) {
            contact.changePhone(phone.trim());
        }
        if (email != null) {
            contact.changeEmail(email.trim());
        }

        contactRepository.update(contact);
    }


    /** Menghapus kontak berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeContact(int id) throws EntityNotFoundException {
        boolean deleted = contactRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Kontak", id);
        }
    }


    /** Mencari kontak yang namanya mengandung kata kunci (case-insensitive). */
    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return contactRepository.findBy(contact -> contact.getName().toLowerCase().contains(lowerKeyword));
    }


    /** Mengurutkan kontak sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Contact> sortContacts(SortOption option) {
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }


    /** Aturan validasi: teks tidak boleh null atau kosong. */
    private void validateNotBlank(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
