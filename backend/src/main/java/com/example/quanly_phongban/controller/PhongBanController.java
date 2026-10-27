
package com.example.quanly_phongban.controller;

import com.example.quanly_phongban.entity.PhongBan;
import com.example.quanly_phongban.repository.PhongBanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/phong-ban")
@CrossOrigin(origins = "http://localhost:5173")
public class PhongBanController {

    private final PhongBanRepository repository;

    public PhongBanController(PhongBanRepository repository) {
        this.repository = repository;
    }

    // 1. Lay danh sach phong ban
    @GetMapping
    public List<PhongBan> getAll() {
        return repository.findAll();
    }

    // 2. Lay phong ban theo ID
    @GetMapping("/{id}")
    public PhongBan getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Khong tim thay phong ban"));
    }

    // 3. Them phong ban
    @PostMapping
    public PhongBan create(@RequestBody PhongBan phongBan) {
        phongBan.setId(null);
        return repository.save(phongBan);
    }

    // 4. Cap nhat phong ban
    @PutMapping("/{id}")
    public PhongBan update(
            @PathVariable Long id,
            @RequestBody PhongBan data) {

        PhongBan existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Khong tim thay phong ban"));

        existing.setTenPhongBan(data.getTenPhongBan());
        existing.setMoTa(data.getMoTa());
        existing.setTrangThai(data.getTrangThai());

        return repository.save(existing);
    }

    // 5. Xoa phong ban
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Khong tim thay phong ban");
        }

        repository.deleteById(id);
    }
}
