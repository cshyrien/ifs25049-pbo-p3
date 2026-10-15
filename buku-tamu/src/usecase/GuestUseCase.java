package usecase;


import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IGuestRepository;
import java.util.List;


/**
 * Use case yang menangani logika bisnis aplikasi buku tamu.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class GuestUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IGuestRepository guestRepository;


    public GuestUseCase(IGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }


    /** Mengambil semua tamu yang tersedia. */
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }


    /** Mendaftarkan tamu baru setelah validasi nama dan tujuan kunjungan. */
    public Guest registerGuest(String name, String purpose) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama tamu tidak boleh kosong!");
        }
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new ValidationException("Tujuan kunjungan tidak boleh kosong!");
        }
        return guestRepository.save(name.trim(), purpose.trim());
    }


    /** Mencari tamu yang namanya mengandung kata kunci (case-insensitive). */
    public List<Guest> searchGuests(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return guestRepository.findBy(guest -> guest.getName().toLowerCase().contains(lowerKeyword));
    }


    /** Menghapus tamu berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeGuest(int id) throws EntityNotFoundException {
        boolean deleted = guestRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Tamu", id);
        }
    }
}
