# Kontak Teman

Aplikasi konsol Java untuk menyimpan kontak teman (nama, telepon, email). Dibangun dengan **Clean Architecture**, **Java Generics**, dan **Java Exceptions**.

## Fitur

- Tambah kontak (validasi nama, format telepon/email)
- Ubah kontak secara parsial (input kosong berarti tidak diubah, `x` membatalkan di setiap prompt; format telepon/email tetap divalidasi)
- Cari kontak berdasarkan nama (case-insensitive)
- Urutkan nama A-Z/Z-A
- Hapus kontak berdasarkan ID

## Struktur Layer

```
src/
├── adapter/
│   ├── presenter/
│   │   └── ContactPresenter.java
│   └── repository/
│       ├── ContactRepository.java
│       └── InMemoryRepository.java
├── domain/
│   ├── entity/
│   │   ├── Contact.java
│   │   └── SortOption.java
│   ├── exception/
│   │   ├── DomainException.java
│   │   ├── EntityNotFoundException.java
│   │   └── ValidationException.java
│   └── repository/
│       ├── IContactRepository.java
│       └── IRepository.java
├── framework/
│   ├── util/
│   │   └── InputUtil.java
│   └── view/
│       └── ContactView.java
├── usecase/
│   └── ContactUseCase.java
└── App.java
```

| Layer | Tanggung jawab |
|---|---|
| `domain` | Entity `Contact`, hierarki exception, dan port repository (`IRepository<T, ID>`). Tidak bergantung pada layer lain. |
| `usecase` | Logika bisnis. Tanpa `System.out`/`System.in`; melempar checked exception domain jika gagal. |
| `adapter` | `InMemoryRepository<T, ID>` (generic) beserta implementasi konkretnya, dan presenter untuk format output. |
| `framework` | View konsol (menu, `try-catch`) dan `InputUtil`. |
| `App.java` | Composition Root: satu-satunya tempat yang mengenal implementasi konkret. |

## Prinsip yang Diterapkan

- **Dependency Rule**: arah dependency `domain ← usecase ← adapter / framework`.
- **Generic Abstraction & Reuse**: `IRepository<T, ID>` dan `InMemoryRepository<T, ID>` dipakai ulang oleh repository spesifik.
- **Structured Domain Exceptions**: `DomainException` (checked), `EntityNotFoundException`, dan `ValidationException`; kegagalan bisnis tidak memakai boolean return code.
- **Enum dengan comparator sendiri**: `SortOption` (`NAME_ASC`, `NAME_DESC`); tiap opsi membawa `Comparator`-nya.
- **Entity immutable**: seluruh field `final`; perubahan data memakai method `with*` yang mengembalikan objek baru (bukan setter/`change*`).
- **Use case tanpa I/O**: semua tampilan dan input ada di layer framework.
- **Presenter terpisah**: format output bukan tanggung jawab entity (entity tidak punya `toString()`).
- **Port & Adapter**: interface di `domain`, implementasi di `adapter`.
- **Salinan defensif**: `findAll()` dan `findBy()` mengembalikan list baru, bukan referensi internal.

## Cara Menjalankan

```
javac -encoding UTF-8 -d output -sourcepath src src/App.java
java -cp output App
```

## Unit Test

Test berada di `test/usecase/ContactUseCaseTest.java` (tanpa framework eksternal, cukup JDK). Dari folder studi kasus:

```
javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/ContactUseCaseTest.java
java -cp output usecase.ContactUseCaseTest
```

(Di Linux/macOS gunakan `src:test` pada `-sourcepath`.) Test mencakup validasi input, update parsial, exception `EntityNotFoundException`, pencarian, pengurutan, dan perilaku entity immutable.

## Catatan Desain: Duplikasi `InMemoryRepository` dan `InputUtil`

`InMemoryRepository` dan `InputUtil` juga ada dengan isi yang sama di studi kasus lain dalam repository ini. Hal ini **disengaja**, bukan kelalaian:

- Setiap studi kasus harus berdiri sendiri di dalam folder `src/` miliknya, sesuai struktur yang ditentukan modul.
- Proyek dikompilasi dan dijalankan per studi kasus dengan `javac -encoding UTF-8 -d output -sourcepath src src/App.java`. Kode di luar `src/` tidak ikut ter-compile, sehingga modul bersama di luar folder ini akan membuat proyek gagal dibangun.
- Di dalam proyek ini sendiri tidak ada duplikasi: `InMemoryRepository<T, ID>` adalah satu basis generic yang dipakai ulang oleh repository konkret, dan `InputUtil` adalah satu-satunya pintu input keyboard.
