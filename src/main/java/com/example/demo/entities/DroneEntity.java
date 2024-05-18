package com.example.demo.entities;

import com.example.demo.enums.DroneModel;
import com.example.demo.enums.DroneState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "DRONE")
public class DroneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "serial", unique = true)
    private String serial;
    @Enumerated(EnumType.STRING)
    private DroneModel model;
    private Short weightLimitGrm;
    private Byte batteryCapacityPercentage;
    @Enumerated(EnumType.STRING)
    private DroneState droneState;

    @ManyToMany
    @JoinTable(name = "drone_medications",
    joinColumns = @JoinColumn(name = "drone_id"),
    inverseJoinColumns = @JoinColumn(name = "medication_id"))
    private Set<MedicationEntity> medications;
}
