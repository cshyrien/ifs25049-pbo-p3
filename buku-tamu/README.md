# Buku Tamu

Aplikasi konsol Java untuk mencatat tamu yang berkunjung beserta tujuan kunjungannya. Dibangun dengan **Clean Architecture**, **Java Generics**, dan **Java Exceptions**.

## Fitur

- Daftarkan tamu (validasi nama dan tujuan kunjungan)
- Cari tamu berdasarkan nama (case-insensitive)
- Hapus tamu berdasarkan ID

## Struktur Layer

```
src/
├── adapter/
│   ├── presenter/
│   │   └── GuestPresenter.java
│   └── repository/
│       ├── GuestRepository.java
│       └── InMemoryRepository.java
├── domain/
│   ├── entity/
│   │   └── Guest.java
│   ├── exception/
│   │   ├── DomainException.java
│   │   ├── EntityNotFoundException.java
│   │   └── ValidationException.java
│   └── repository/
│       ├── IGuestRepository.java
│       └── IRepository.java
├── framework/
│   ├── util/
│   │   └── InputUtil.java
│   └── view/
│       └── GuestView.java
├── usecase/
│   └── GuestUseCase.java
└── App.java
```

| Layer | Tanggung jawab |
|---|---|
| `domain` | Entity `Guest`, hierarki exception, dan port repository (`IRepository<T, ID>`). Tidak bergantung pada layer lain. |
| `usecase` | Logika bisnis. Tanpa `System.out`/`System.in`; melempar checked exception domain jika gagal. |
| `adapter` | `InMemoryRepository<T, ID>` (generic) beserta implementasi konkretnya, dan presenter untuk format output. |
| `framework` | View konsol (menu, `try-catch`) dan `InputUtil`. |
| `App.java` | Composition Root: satu-satunya tempat yang mengenal implementasi konkret. |

## Prinsip yang Diterapkan

- **Dependency Rule**: arah dependency `domain ← usecase ← adapter / framework`.
- **Generic Abstraction & Reuse**: `IRepository<T, ID>` dan `InMemoryRepository<T, ID>` dipakai ulang oleh repository spesifik.
- **Structured Domain Exceptions**: `DomainException` (checked), `EntityNotFoundException`, dan `ValidationException`; kegagalan bisnis tidak memakai boolean return code.
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

Test berada di `test/usecase/GuestUseCaseTest.java` (tanpa framework eksternal, cukup JDK). Dari folder studi kasus:

```
javac -encoding UTF-8 -d output -sourcepath "src;test" test/usecase/GuestUseCaseTest.java
java -cp output usecase.GuestUseCaseTest
```

(Di Linux/macOS gunakan `src:test` pada `-sourcepath`.) Test mencakup validasi input, update parsial, exception `EntityNotFoundException`, pencarian, pengurutan, dan perilaku entity immutable.

## Catatan Desain: Duplikasi `InMemoryRepository` dan `InputUtil`

`InMemoryRepository` dan `InputUtil` juga ada dengan isi yang sama di studi kasus lain dalam repository ini. Hal ini **disengaja**, bukan kelalaian:

- Setiap studi kasus harus berdiri sendiri di dalam folder `src/` miliknya, sesuai struktur yang ditentukan modul.
- Proyek dikompilasi dan dijalankan per studi kasus dengan `javac -encoding UTF-8 -d output -sourcepath src src/App.java`. Kode di luar `src/` tidak ikut ter-compile, sehingga modul bersama di luar folder ini akan membuat proyek gagal dibangun.
- Di dalam proyek ini sendiri tidak ada duplikasi: `InMemoryRepository<T, ID>` adalah satu basis generic yang dipakai ulang oleh repository konkret, dan `InputUtil` adalah satu-satunya pintu input keyboard.
