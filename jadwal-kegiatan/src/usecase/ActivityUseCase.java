package usecase;


import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;


/**
 * Use case yang menangani logika bisnis aplikasi jadwal kegiatan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class ActivityUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IActivityRepository activityRepository;


    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }


    /** Mengambil semua kegiatan yang tersedia. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }


    /** Menambahkan kegiatan baru setelah validasi judul, hari, dan waktu. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        validateNotBlank(title, "Judul kegiatan tidak boleh kosong!");
        validateNotBlank(day, "Hari kegiatan tidak boleh kosong!");
        validateNotBlank(time, "Waktu kegiatan tidak boleh kosong!");
        validateNotDuplicate(null, title.trim(), day.trim(), time.trim());
        return activityRepository.save(title.trim(), day.trim(), time.trim());
    }


    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan.
     * Parameter {@code null} berarti field tersebut tidak diubah (update parsial).
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kegiatan", id));

        // Validasi seluruh field yang diubah sebelum ada perubahan yang diterapkan
        if (title != null) {
            validateNotBlank(title, "Judul kegiatan tidak boleh kosong!");
        }
        if (day != null) {
            validateNotBlank(day, "Hari kegiatan tidak boleh kosong!");
        }
        if (time != null) {
            validateNotBlank(time, "Waktu kegiatan tidak boleh kosong!");
        }
        // Cek duplikasi terhadap kegiatan lain berdasarkan nilai akhir setelah perubahan
        validateNotDuplicate(id,
                title != null ? title.trim() : activity.getTitle(),
                day != null ? day.trim() : activity.getDay(),
                time != null ? time.trim() : activity.getTime());

        if (title != null) {
            activity.changeTitle(title.trim());
        }
        if (day != null) {
            activity.changeDay(day.trim());
        }
        if (time != null) {
            activity.changeTime(time.trim());
        }

        activityRepository.update(activity);
    }


    /** Menghapus kegiatan berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        boolean deleted = activityRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Kegiatan", id);
        }
    }


    /** Mencari kegiatan yang judulnya mengandung kata kunci (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return activityRepository.findBy(
                activity -> activity.getTitle().toLowerCase().contains(lowerKeyword));
    }


    /** Mengurutkan kegiatan sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }


    /**
     * Aturan validasi: kegiatan dengan judul, hari, dan waktu yang sama (case-insensitive) tidak boleh ganda.
     *
     * @param ownId ID kegiatan yang sedang diubah (dikecualikan), atau null saat menambah
     */
    private void validateNotDuplicate(Integer ownId, String title, String day, String time)
            throws ValidationException {
        boolean exists = !activityRepository.findBy(a -> (ownId == null || a.getId() != ownId)
                && a.getTitle().equalsIgnoreCase(title)
                && a.getDay().equalsIgnoreCase(day)
                && a.getTime().equalsIgnoreCase(time)).isEmpty();
        if (exists) {
            throw new ValidationException("Kegiatan dengan judul, hari, dan waktu yang sama sudah ada!");
        }
    }


    /** Aturan validasi: teks tidak boleh null atau kosong. */
    private void validateNotBlank(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
