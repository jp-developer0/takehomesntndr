package com.santander.banking.util;

import com.santander.banking.dto.CuentaBancariaRequestDTO;
import com.santander.banking.dto.CuentaBancariaResponseDTO;
import com.santander.banking.entity.CuentaBancaria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversiones entre entidades CuentaBancaria y DTOs.
 * Implementa el patrón Mapper para separar las responsabilidades de conversión.
 * Sigue el principio de Responsabilidad Única (SRP) de SOLID.
 */
@Component
public class CuentaBancariaMapper {

    /**
     * Convierte un RequestDTO a una entidad CuentaBancaria.
     * Utiliza el patrón Builder de la entidad.
     * 
     * @param requestDTO DTO con los datos de entrada
     * @return Entidad CuentaBancaria construida
     */
    public CuentaBancaria toEntity(CuentaBancariaRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return CuentaBancaria.builder()
                .numeroCuenta(requestDTO.getNumeroCuenta())
                .titular(requestDTO.getTitular())
                .saldo(requestDTO.getSaldo())
                .tipoCuenta(requestDTO.getTipoCuenta())
                .moneda(requestDTO.getMoneda())
                .build();
    }

    /**
     * Convierte una entidad CuentaBancaria a un ResponseDTO.
     * 
     * @param entity Entidad CuentaBancaria
     * @return DTO de respuesta con todos los datos
     */
    public CuentaBancariaResponseDTO toResponseDTO(CuentaBancaria entity) {
        if (entity == null) {
            return null;
        }

        return new CuentaBancariaResponseDTO(
                entity.getId(),
                entity.getNumeroCuenta(),
                entity.getTitular(),
                entity.getSaldo(),
                entity.getTipoCuenta(),
                entity.getMoneda(),
                entity.getFechaCreacion(),
                entity.getFechaActualizacion(),
                entity.getActiva()
        );
    }

    /**
     * Convierte una lista de entidades a una lista de ResponseDTOs.
     * 
     * @param entities Lista de entidades
     * @return Lista de DTOs de respuesta
     */
    public List<CuentaBancariaResponseDTO> toResponseDTOList(List<CuentaBancaria> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una entidad existente con los datos de un RequestDTO.
     * Mantiene los datos inmutables como ID, fechas de creación, etc.
     * 
     * @param entity Entidad existente a actualizar
     * @param requestDTO DTO con los nuevos datos
     */
    public void updateEntityFromDTO(CuentaBancaria entity, CuentaBancariaRequestDTO requestDTO) {
        if (entity == null || requestDTO == null) {
            return;
        }

        // Actualizar solo los campos modificables
        if (requestDTO.getTitular() != null) {
            entity.setTitular(requestDTO.getTitular());
        }
        
        if (requestDTO.getSaldo() != null) {
            entity.setSaldo(requestDTO.getSaldo());
        }
        
        // Nota: El número de cuenta normalmente no se actualiza después de la creación
        // pero se podría permitir en casos específicos de negocio
        
        // El tipo de cuenta y moneda generalmente no se cambian después de la creación
        // por razones de integridad de datos bancarios, pero se podrían permitir
        // con validaciones adicionales
    }

    /**
     * Crea un DTO de respuesta básico con información mínima.
     * Útil para operaciones que no requieren todos los detalles.
     * 
     * @param entity Entidad CuentaBancaria
     * @return DTO con información básica
     */
    public CuentaBancariaResponseDTO toBasicResponseDTO(CuentaBancaria entity) {
        if (entity == null) {
            return null;
        }

        CuentaBancariaResponseDTO dto = new CuentaBancariaResponseDTO();
        dto.setId(entity.getId());
        dto.setNumeroCuenta(entity.getNumeroCuenta());
        dto.setTitular(entity.getTitular());
        dto.setSaldo(entity.getSaldo());
        dto.setTipoCuenta(entity.getTipoCuenta());
        dto.setActiva(entity.getActiva());
        
        return dto;
    }

    /**
     * Valida que un RequestDTO tenga los datos mínimos requeridos.
     * 
     * @param requestDTO DTO a validar
     * @return true si es válido, false en caso contrario
     */
    public boolean isValidRequestDTO(CuentaBancariaRequestDTO requestDTO) {
        return requestDTO != null &&
               requestDTO.getNumeroCuenta() != null &&
               !requestDTO.getNumeroCuenta().trim().isEmpty() &&
               requestDTO.getTitular() != null &&
               !requestDTO.getTitular().trim().isEmpty() &&
               requestDTO.getTipoCuenta() != null;
    }
} 