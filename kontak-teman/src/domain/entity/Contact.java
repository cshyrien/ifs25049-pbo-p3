package domain.entity;


/**
 * Entity inti yang merepresentasikan satu kontak teman.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan maupun penyimpanan. Bersifat immutable: seluruh field final,
 * dan perubahan dilakukan lewat method with* yang mengembalikan objek baru.
 */
public class Contact {
    /** ID unik, tidak berubah setelah dibuat. */
    private final int id;


    /** Nama kontak. */
    private final String name;


    /** Nomor telepon kontak. */
    private final String phone;


    /** Alamat email kontak. */
    private final String email;


    public Contact(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }


    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getPhone() {
        return phone;
    }


    public String getEmail() {
        return email;
    }


    /** Mengembalikan salinan baru dengan nama kontak yang diganti; objek ini tidak berubah. */
    public Contact withName(String newName) {
        return new Contact(id, newName, phone, email);
    }


    /** Mengembalikan salinan baru dengan nomor telepon kontak yang diganti; objek ini tidak berubah. */
    public Contact withPhone(String newPhone) {
        return new Contact(id, name, newPhone, email);
    }


    /** Mengembalikan salinan baru dengan alamat email kontak yang diganti; objek ini tidak berubah. */
    public Contact withEmail(String newEmail) {
        return new Contact(id, name, phone, newEmail);
    }
}
