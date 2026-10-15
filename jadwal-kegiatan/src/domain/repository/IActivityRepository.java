package domain.repository;


import domain.entity.Activity;


/**
 * Port spesifik Activity yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan kegiatan baru dengan penomoran ID otomatis.
 */
public interface IActivityRepository extends IRepository<Activity, Integer> {
    /**
     * Menyimpan kegiatan baru; repository bertanggung jawab membuat ID unik.
     *
     * @param title judul kegiatan
     * @param day   hari pelaksanaan
     * @param time  waktu pelaksanaan
     * @return kegiatan yang tersimpan (lengkap dengan ID)
     */
    Activity save(String title, String day, String time);
}
