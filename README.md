# ifs25049-java-exception-generic

Lima studi kasus aplikasi konsol Java yang dibangun dengan Clean Architecture, Java Generics, dan Java Exceptions:

| Studi kasus | Folder |
|---|---|
| Catatan Keuangan | [`catatan-keuangan`](catatan-keuangan/) |
| Buku Tamu | [`buku-tamu`](buku-tamu/) |
| Inventaris Barang | [`inventaris-barang`](inventaris-barang/) |
| Jadwal Kegiatan | [`jadwal-kegiatan`](jadwal-kegiatan/) |
| Kontak Teman | [`kontak-teman`](kontak-teman/) |

Setiap folder berdiri sendiri (berisi `src/` dan `README.md`) dan dikompilasi terpisah:

```
javac -encoding UTF-8 -d output -sourcepath src src/App.java
java -cp output App
```

**Catatan:** `InMemoryRepository` dan `InputUtil` ada di tiap studi kasus dengan isi yang sama. Ini disengaja agar setiap proyek dapat dikompilasi sendiri tanpa bergantung pada kode di luar foldernya.
