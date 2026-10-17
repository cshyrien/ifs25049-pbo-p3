package framework.util;


import domain.exception.ValidationException;
import java.util.Scanner;


/**
 * Utility untuk membaca dan mengonversi input keyboard.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 */
public class InputUtil {
    /** Kata kunci yang diketik user untuk membatalkan operasi atau keluar dari menu. */
    public static final String CANCEL_KEY = "x";


    /** Scanner bersama untuk seluruh aplikasi (satu instance cukup). */
    private static final Scanner scanner = new Scanner(System.in);


    private InputUtil() {
    }


    /**
     * Menampilkan prompt dan membaca satu baris input dari user.
     * Jika input sudah habis (EOF), mengembalikan {@link #CANCEL_KEY} agar aplikasi
     * berhenti dengan rapi dan tidak melempar exception.
     *
     * @param info label yang ditampilkan sebelum input
     * @return teks yang diketik user
     */
    public static String input(String info) {
        System.out.print(info + " : ");
        if (!scanner.hasNextLine()) {
            return CANCEL_KEY;
        }
        return scanner.nextLine();
    }


    /** Memeriksa apakah input user berarti pembatalan operasi. */
    public static boolean isCancel(String input) {
        return CANCEL_KEY.equals(input.trim());
    }


    /**
     * Mengonversi teks menjadi bilangan bulat.
     *
     * @param errorMessage pesan yang dipakai jika teks bukan angka valid
     * @throws ValidationException jika teks bukan bilangan bulat
     */
    public static int parseInt(String value, String errorMessage) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage, e);
        }
    }


    /**
     * Mengonversi teks menjadi bilangan bulat panjang.
     *
     * @param errorMessage pesan yang dipakai jika teks bukan angka valid
     * @throws ValidationException jika teks bukan bilangan bulat
     */
    public static long parseLong(String value, String errorMessage) throws ValidationException {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(errorMessage, e);
        }
    }
}
