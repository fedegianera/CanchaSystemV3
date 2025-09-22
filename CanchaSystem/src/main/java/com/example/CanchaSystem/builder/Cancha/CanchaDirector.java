package com.example.CanchaSystem.builder.Cancha;

import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@AllArgsConstructor
@Component
public class CanchaDirector {
    private final CanchaBuilder builder;

    public void constructFullCancha(String name, String address, double amount,
                                    LocalTime opening, LocalTime closing,
                                    boolean hasRoof, boolean canShower,
                                    boolean working, Brand brand, CanchaType type) {
        builder.createNewCancha();
        builder.buildName(name);
        builder.buildAddress(address);
        builder.buildTotalAmount(amount);
        builder.buildOpeningHour(opening);
        builder.buildClosingHour(closing);
        builder.buildHasRoof(hasRoof);
        builder.buildCanShower(canShower);
        builder.buildWorking(working);
        builder.buildBrand(brand);
        builder.buildCanchaType(type);
    }

    public Cancha getCancha() {
        return builder.getCancha();
    }
}
