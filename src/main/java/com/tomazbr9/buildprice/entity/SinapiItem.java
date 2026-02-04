package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_sinapi_item")
public class SinapiItem {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cod_sinapi", nullable = false)
    private String codSinapi;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String classification;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String uf;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "tax_relief", nullable = false)
    private String taxRelief;

    public SinapiItem(){

    }

    public SinapiItem(String codSinapi, String description, String classification, String unit, String uf, BigDecimal price, String taxRelief) {
        this.codSinapi = codSinapi;
        this.description = description;
        this.classification = classification;
        this.unit = unit;
        this.uf = uf;
        this.price = price;
        this.taxRelief = taxRelief;
    }

    public SinapiItem(UUID id, String codSinapi, String description, String classification, String unit, String uf, BigDecimal price, String taxRelief) {
        this.id = id;
        this.codSinapi = codSinapi;
        this.description = description;
        this.classification = classification;
        this.unit = unit;
        this.uf = uf;
        this.price = price;
        this.taxRelief = taxRelief;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getCodSinapi() {
        return codSinapi;
    }

    public void setCodSinapi(String codSinapi) {
        this.codSinapi = codSinapi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf){
        this.uf = uf;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public String getTaxRelief() {
        return taxRelief;
    }

    public void setTaxRelief(String taxRelief) {
        this.taxRelief = taxRelief;
    }
}
