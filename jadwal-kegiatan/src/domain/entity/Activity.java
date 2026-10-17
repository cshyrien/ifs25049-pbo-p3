package domain.entity;


/**
 * Entity inti yang merepresentasikan satu kegiatan pada jadwal.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan maupun penyimpanan. Bersifat immutable: seluruh field final,
 * dan perubahan dilakukan lewat method with* yang mengembalikan objek baru.
 */
public class Activity {
    /** ID unik, tidak berubah setelah dibuat. */
    private final int id;


    /** Judul kegiatan. */
    private final String title;


    /** Hari pelaksanaan kegiatan (mis. Senin). */
    private final String day;


    /** Waktu pelaksanaan kegiatan (mis. 08:00). */
    private final String time;


    public Activity(int id, String title, String day, String time) {
        this.id = id;
        this.title = title;
        this.day = day;
        this.time = time;
    }


    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getDay() {
        return day;
    }


    public String getTime() {
        return time;
    }


    /** Mengembalikan salinan baru dengan judul kegiatan yang diganti; objek ini tidak berubah. */
    public Activity withTitle(String newTitle) {
        return new Activity(id, newTitle, day, time);
    }


    /** Mengembalikan salinan baru dengan hari pelaksanaan kegiatan (mis. Senin) yang diganti; objek ini tidak berubah. */
    public Activity withDay(String newDay) {
        return new Activity(id, title, newDay, time);
    }


    /** Mengembalikan salinan baru dengan waktu pelaksanaan kegiatan (mis. 08:00) yang diganti; objek ini tidak berubah. */
    public Activity withTime(String newTime) {
        return new Activity(id, title, day, newTime);
    }
}
