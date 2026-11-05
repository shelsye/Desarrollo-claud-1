package cl.duoc.mesatech.solicitudes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "categorias_locales",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "categoria_id_origen")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id_origen", nullable = false)
    private Long categoriaIdOrigen;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "fecha_evento", nullable = false)
    private String fechaEvento;
}