package domain.entity;


import java.util.Comparator;


/**
 * Kriteria pengurutan kegiatan.
 * Setiap opsi membawa comparator-nya sendiri sehingga logika pengurutan
 * terpusat di domain, bukan tersebar sebagai angka ajaib di view/use case.
 */
public enum SortOption {
    /** Urutkan berdasarkan hari (Senin-Minggu), lalu waktu. */
    DAY(Comparator.comparingInt((Activity activity) -> dayOrder(activity.getDay()))
            .thenComparingInt(activity -> timeOrder(activity.getTime()))),


    /** Urutkan berdasarkan waktu (paling awal dulu), lalu hari. */
    TIME(Comparator.comparingInt((Activity activity) -> timeOrder(activity.getTime()))
            .thenComparingInt(activity -> dayOrder(activity.getDay()))),


    /** Urutkan judul dari A ke Z (case-insensitive). */
    TITLE_ASC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER)),


    /** Urutkan judul dari Z ke A (case-insensitive). */
    TITLE_DESC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());


    /** Comparator yang digunakan untuk mengurutkan daftar kegiatan. */
    private final Comparator<Activity> comparator;


    SortOption(Comparator<Activity> comparator) {
        this.comparator = comparator;
    }


    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Activity> comparator() {
        return comparator;
    }


    /** Mengubah nama hari menjadi urutan 1 (Senin) sampai 7 (Minggu); hari tak dikenal di paling akhir. */
    private static int dayOrder(String day) {
        if (day == null) {
            return Integer.MAX_VALUE;
        }
        return switch (day.trim().toLowerCase()) {
            case "senin" -> 1;
            case "selasa" -> 2;
            case "rabu" -> 3;
            case "kamis" -> 4;
            case "jumat", "jum'at" -> 5;
            case "sabtu" -> 6;
            case "minggu" -> 7;
            default -> Integer.MAX_VALUE;
        };
    }


    /** Mengubah waktu berformat jam:menit menjadi total menit; format tak dikenal di paling akhir. */
    private static int timeOrder(String time) {
        if (time == null) {
            return Integer.MAX_VALUE;
        }
        try {
            String[] parts = time.trim().split("[:.]");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Integer.MAX_VALUE;
        }
    }
}
