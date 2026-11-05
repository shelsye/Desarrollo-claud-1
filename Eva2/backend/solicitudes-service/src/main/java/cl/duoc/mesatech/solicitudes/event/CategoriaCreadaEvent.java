package cl.duoc.mesatech.solicitudes.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCreadaEvent {

    private String eventId;
    private String tipoEvento;
    private String fechaEvento;
    private Long categoriaId;
    private String nombre;
}