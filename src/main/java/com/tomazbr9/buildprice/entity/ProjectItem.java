package com.tomazbr9.buildprice.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_project_items")
public class ProjectItem implements Serializable {

    private static final long seriaVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = true)
    private UUID id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sinapi_item_id", nullable = false)
    private SinapiItem sinapiItem;

    public ProjectItem(){

    }

    public ProjectItem(UUID id, Integer quantity, BigDecimal price, Project project, SinapiItem sinapiItem) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.project = project;
        this.sinapiItem = sinapiItem;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public SinapiItem getSinapiItem() {
        return sinapiItem;
    }

    public void setSinapiItem(SinapiItem sinapiItem) {
        this.sinapiItem = sinapiItem;
    }
}
