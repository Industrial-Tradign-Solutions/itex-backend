package com.itradingsolutions.itex.api.common.consecutive.models.entities;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "itex_consecutive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsecutiveEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4062048083854819883L;

    @EmbeddedId
    private ConsecutiveId id;

    public ConsecutiveEntity(ConsecutiveModule module, ConsecutiveDepartment department, String consecutive) {
        if (module == ConsecutiveModule.INV) return;

        if (consecutive == null || module == null) throw new IllegalArgumentException("Consecutive and module cannot be null");

        String moduleSuffix = module.name();
        if (!consecutive.endsWith(moduleSuffix)) throw new IllegalArgumentException("Consecutive does not match the provided module suffix");

        // Remueve el sufijo del módulo para analizar el resto
        String withoutModule = consecutive.substring(0, consecutive.length() - moduleSuffix.length());

        // Extrae partes fijas
        String clientCode = withoutModule.substring(0, 3);
        String year = withoutModule.substring(3, 5);
        String month = withoutModule.substring(5, 7);

        // Lo que queda es el contador (puede tener longitud variable)
        String counterStr = withoutModule.substring(7);
        this.id = new ConsecutiveId(department, clientCode, year, month, Integer.parseInt(counterStr), module);
    }

    public String getConsecutive() {
        return
                this.getId().getClientCode()
                + this.getId().getYear()
                + this.getId().getMonth()
                + String.format("%02d", this.getId().getNumber())
                + this.getId().getModule().name();
    }
}
