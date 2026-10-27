
package com.example.quanly_phongban.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "phong_ban")
public class PhongBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_phong_ban", nullable = false, length = 100)
    private String tenPhongBan;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    public PhongBan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }
}
