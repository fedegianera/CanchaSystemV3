package com.example.CanchaSystem.builder.Cancha;

import com.example.CanchaSystem.model.Brand;
import com.example.CanchaSystem.model.Cancha;
import com.example.CanchaSystem.model.CanchaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CanchaBuilder {
    private Cancha cancha;

    public void createNewCancha() {
        this.cancha = new Cancha();
    }

    public Cancha getCancha() {
        Cancha result = this.cancha;
        this.cancha = new Cancha();
        return result;
    }


    public void buildName(String name) {
        this.cancha.setName(name);
    }

    public void buildAddress (String address) {
        this.cancha.setAddress(address);
    }

    public void buildTotalAmount(double amount) {
        this.cancha.setTotalAmount(amount);
    }

    public void buildOpeningHour(LocalTime openingHour) {
        this.cancha.setOpeningHour(openingHour);
    }

    public void buildClosingHour(LocalTime closingHour) {
        this.cancha.setClosingHour(closingHour);
    }

    public void buildHasRoof(boolean hasRoof) {
        this.cancha.setHasRoof(hasRoof);
    }

    public void buildCanShower(boolean canShower) {
        this.cancha.setCanShower(canShower);
    }

    public void buildWorking(boolean working) {
        this.cancha.setWorking(working);
    }

    public void buildBrand(Brand brand) {
        this.cancha.setBrand(brand);
    }

    public void buildCanchaType(CanchaType canchaType) {
        this.cancha.setCanchaType(canchaType);
    }
}
