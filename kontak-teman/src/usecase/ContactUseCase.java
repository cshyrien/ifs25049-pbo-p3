package usecase;


import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Use case yang menangani logika bisnis aplikasi kontak teman.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ContactUseCase {
    /** Nomor telepon: angka, boleh diawali "+", boleh memuat spasi atau tanda hubung (6-20 karakter). */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9][0-9\\- ]{4,18}[0-9]$");


    /** Email: bagian lokal, "@", lalu domain yang memuat titik (tanpa spasi). */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$");


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
        validatePhone(phone);
        validateEmail(email);
        validateNotDuplicate(null, phone.trim(), email.trim());
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
        if (phone != null) {
            validatePhone(phone);
        }
        if (email != null) {
            validateEmail(email);
        }
        // Cek duplikasi terhadap kontak lain berdasarkan nilai telepon/email akhir
        validateNotDuplicate(id,
                phone != null ? phone.trim() : contact.getPhone(),
                email != null ? email.trim() : contact.getEmail());

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


    /** Aturan validasi: nomor telepon harus berformat angka yang wajar. */
    private void validatePhone(String phone) throws ValidationException {
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Format nomor telepon tidak valid!");
        }
    }


    /** Aturan validasi: email harus berformat nama@domain.tld. */
    private void validateEmail(String email) throws ValidationException {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Format email tidak valid!");
        }
    }


    /**
     * Aturan validasi: telepon dan email tidak boleh sama dengan kontak lain.
     *
     * @param ownId ID kontak yang sedang diubah (dikecualikan dari pengecekan), atau null saat menambah
     */
    private void validateNotDuplicate(Integer ownId, String phone, String email) throws ValidationException {
        boolean phoneUsed = !contactRepository.findBy(
                c -> isOtherContact(c, ownId) && c.getPhone().equals(phone)).isEmpty();
        if (phoneUsed) {
            throw new ValidationException("Nomor telepon sudah digunakan kontak lain!");
        }
        boolean emailUsed = !contactRepository.findBy(
                c -> isOtherContact(c, ownId) && c.getEmail().equalsIgnoreCase(email)).isEmpty();
        if (emailUsed) {
            throw new ValidationException("Email sudah digunakan kontak lain!");
        }
    }


    /** Kontak dianggap "lain" jika bukan kontak yang sedang diubah (ownId null = tambah baru). */
    private boolean isOtherContact(Contact contact, Integer ownId) {
        return ownId == null || contact.getId() != ownId;
    }


    /** Aturan validasi: teks tidak boleh null atau kosong. */
    private void validateNotBlank(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
