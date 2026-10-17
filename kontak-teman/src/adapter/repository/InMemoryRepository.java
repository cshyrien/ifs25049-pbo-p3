package adapter.repository;


import domain.repository.IRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Base generic in-memory repository yang mengimplementasikan {@link IRepository}.
 * Menghilangkan duplikasi kode penyimpanan dan pencarian untuk berbagai entitas.
 * Data disimpan di {@link LinkedHashMap} sehingga pencarian, penggantian, dan
 * penghapusan berdasarkan ID berjalan O(1) dengan urutan penyimpanan tetap terjaga.
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe data identifier unik entitas
 */
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    /** Penyimpanan data entitas di memori, dikunci berdasarkan ID. */
    protected final Map<ID, T> data = new LinkedHashMap<>();


    /** Fungsi untuk mengekstrak ID dari objek entitas. */
    private final Function<T, ID> idExtractor;


    protected InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }


    @Override
    public List<T> findAll() {
        // Salinan defensif agar data internal tidak termutasi dari luar
        return new ArrayList<>(data.values());
    }


    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }


    @Override
    public T save(T entity) {
        data.put(idExtractor.apply(entity), entity);
        return entity;
    }


    @Override
    public boolean deleteById(ID id) {
        return data.remove(id) != null;
    }


    @Override
    public void update(T entity) {
        // Mengganti entitas lama yang memiliki ID sama; urutan penyimpanan tetap
        data.replace(idExtractor.apply(entity), entity);
    }


    @Override
    public List<T> findBy(Predicate<T> predicate) {
        // Hasil berupa list baru, bukan referensi ke penyimpanan internal
        return data.values().stream()
                .filter(predicate)
                .toList();
    }
}
