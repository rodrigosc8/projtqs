package pt.rodrigo.hw1_tqs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "municipality")
public class Municipality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(nullable = false, unique = true)
    @JsonProperty("code")
    private String code;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @JsonProperty("name")
    private String name;

    @Size(max = 255)  // ✅ REMOVER @NotNull DAQUI
    @Column(nullable = true)  // ✅ MUDAR PARA nullable = true
    @JsonProperty("district")
    private String district;

    public Municipality() {
    }

    public Municipality(String code, String name, String district) {
        this.code = code;
        this.name = name;
        this.district = district;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
