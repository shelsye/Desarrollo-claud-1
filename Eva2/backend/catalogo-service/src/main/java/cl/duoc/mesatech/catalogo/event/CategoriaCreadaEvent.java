package cl.duoc.mesatech.catalogo.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaCreadaEvent {

    private String eventId;
    private String tipoEvento;
    private String fechaEvento;
    private Long categoriaId;
    private String nombre;
}
