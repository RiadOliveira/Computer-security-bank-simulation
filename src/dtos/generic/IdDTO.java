package dtos.generic;

import utils.ConsolePrinter;
import java.util.UUID;

import dtos.DTO;

public class IdDTO extends DTO {
    private final UUID id;

    public IdDTO(UUID id) {
        this.id = id;
    }

    @Override
    public void print() {
        ConsolePrinter.println(id);
    }

    public UUID getId() {
        return id;
    }
}
